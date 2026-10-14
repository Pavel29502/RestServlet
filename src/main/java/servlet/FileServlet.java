package servlet;

import DAO.FileDAO;
import service.EventService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.File;
import service.FileService;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
@WebServlet("/api/files/*")
public class FileServlet extends HttpServlet {

    private FileService fileService = new FileService();

    private EventService eventService = new EventService();

    private FileDAO fileDAO = new FileDAO();

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    context.serialize(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo != null && pathInfo.startsWith("/download/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length > 2) {
                    Integer fileId = Integer.parseInt(parts[2]);
                    downloadFile(fileId, resp);
                    return;
                }
            }

            if (pathInfo == null || pathInfo.equals("/")) {
                List<File> files = fileService.getAllFiles();
                resp.getWriter().write(gson.toJson(files));
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length > 1) {
                Integer id = Integer.parseInt(parts[1]);
                File file = fileService.getFileById(id);
                resp.getWriter().write(gson.toJson(file));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Part filePart = req.getPart("file");

        if (filePart == null || filePart.getSize() == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Файл не передан\"}");
            return;
        }

            String userIdStr = req.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"userId не указан\"}");
                return;
        }
            Integer userId = Integer.parseInt(userIdStr);

            String fileName = filePart.getSubmittedFileName();

            String uploadDir = getServletContext().getRealPath("") + java.io.File.separator + "uploads";
            java.io.File uploadFolder = new java.io.File(uploadDir);

            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String filePath = uploadDir + java.io.File.separator + fileName;
            filePart.write(filePath);

            fileService.createFile(fileName, filePath);

            File savedFile = fileDAO.findByFilePath(filePath);
            if (savedFile != null) {
                eventService.createEvent(userId, savedFile.getId());
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Файл загружен и событие создано\"}");
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID не указан\"}");
                return;
            }

            String[] parts = pathInfo.split("/");
            Integer id = Integer.parseInt(parts[1]);

            BufferedReader reader = req.getReader();
            File fileData = gson.fromJson(reader, File.class);

            fileService.updateFile(id, fileData.getName(), fileData.getFilePath());

            resp.getWriter().write("{\"message\": \"Файл обновлен\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID не указан\"}");
                return;
            }

            String[] parts = pathInfo.split("/");
            Integer id = Integer.parseInt(parts[1]);

            fileService.deleteFile(id);

            resp.getWriter().write("{\"message\": \"Файл удален\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void downloadFile(Integer fileId, HttpServletResponse resp) throws IOException {
        File file = fileService.getFileById(fileId);
        if (file == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Файл не найден\"}");
            return;
        }

        Path filePath = Paths.get(file.getFilePath());
        if (!Files.exists(filePath)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Файл не найден на диске\"}");
            return;
        }

        resp.setContentType(Files.probeContentType(filePath));
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        resp.setContentLengthLong(Files.size(filePath));

        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            fis.transferTo(resp.getOutputStream());
        }
    }
}





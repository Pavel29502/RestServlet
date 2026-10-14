package servlet;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Event;
import service.EventService;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/events/*")
public class EventServlet extends HttpServlet {

    private EventService eventService = new EventService();

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

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Event> events = eventService.getAllEvents();
                resp.getWriter().write(gson.toJson(events));
                return;
            }

            if (pathInfo.startsWith("/user/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length > 2) {
                    Integer userId = Integer.parseInt(parts[2]);
                    List<Event> events = eventService.getEventsByUser(userId);
                    resp.getWriter().write(gson.toJson(events));
                    return;
                }
            }

            if (pathInfo.startsWith("/file/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length > 2) {
                    Integer fileId = Integer.parseInt(parts[2]);
                    List<Event> events = eventService.getEventsByFile(fileId);
                    resp.getWriter().write(gson.toJson(events));
                    return;
                }
            }

            String[] parts = pathInfo.split("/");
            if (parts.length > 1) {
                Integer id = Integer.parseInt(parts[1]);
                Event event = eventService.getEventById(id);
                resp.getWriter().write(gson.toJson(event));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);


            if (jsonObject == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Тело запроса пустое\"}");
                return;
            }

            if (!jsonObject.has("user") || jsonObject.get("user").isJsonNull()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Поле 'user' обязательно\"}");
                return;
            }

            if (!jsonObject.has("file") || jsonObject.get("file").isJsonNull()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Поле 'file' обязательно\"}");
                return;
            }

            JsonObject userObj = jsonObject.getAsJsonObject("user");
            if (!userObj.has("id") || userObj.get("id").isJsonNull()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Поле 'user.id' обязательно\"}");
                return;
            }

            JsonObject fileObj = jsonObject.getAsJsonObject("file");
            if (!fileObj.has("id") || fileObj.get("id").isJsonNull()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Поле 'file.id' обязательно\"}");
                return;
            }

            int userId = userObj.get("id").getAsInt();
            int fileId = fileObj.get("id").getAsInt();

            eventService.createEvent(userId, fileId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Событие создано\"}");

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат JSON\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
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
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Тело запроса пустое\"}");
                return;
            }

            Integer newUserId = null;
            Integer newFileId = null;

            if (jsonObject.has("user") && !jsonObject.get("user").isJsonNull()) {
                newUserId = jsonObject.get("user").getAsJsonObject().get("id").getAsInt();
            }
            if (jsonObject.has("file") && !jsonObject.get("file").isJsonNull()) {
                newFileId = jsonObject.get("file").getAsJsonObject().get("id").getAsInt();
            }

            eventService.updateEvent(id, newUserId, newFileId);

            resp.getWriter().write("{\"message\": \"Событие обновлено\"}");
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

            eventService.deleteEvent(id);

            resp.getWriter().write("{\"message\": \"Событие удалено\"}");
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
}


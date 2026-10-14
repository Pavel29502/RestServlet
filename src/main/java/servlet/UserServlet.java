package servlet;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import com.google.gson.JsonSyntaxException;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet{
    private UserService userService = new UserService();

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
                List<User> users = userService.getAllUsers();
                resp.getWriter().write(gson.toJson(users));
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length > 1) {
                Integer id = Integer.parseInt(parts[1]);
                User user = userService.getUserById(id);
                resp.getWriter().write(gson.toJson(user));
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
            User userData = gson.fromJson(reader, User.class);

            // ← ДОБАВЛЕНО: проверка на null и пустое имя
            if (userData == null || userData.getName() == null || userData.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Имя пользователя не может быть пустым\"}");
                return;
            }

            userService.createUser(userData.getName());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Пользователь создан\"}");

        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат JSON\"}");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("уже существует")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);  // ← 409
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);  // ← 404
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
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
            User userData = gson.fromJson(reader, User.class);

            if (userData == null || userData.getName() == null || userData.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Имя пользователя не может быть пустым\"}");
                return;
            }

            userService.updateUser(id, userData.getName());

            resp.getWriter().write("{\"message\": \"Пользователь обновлен\"}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат JSON\"}");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("уже существует")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);  // ← 409
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);  // ← 404
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
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

            userService.deleteUser(id);

            resp.getWriter().write("{\"message\": \"Пользователь удален\"}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Неверный формат ID\"}");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}








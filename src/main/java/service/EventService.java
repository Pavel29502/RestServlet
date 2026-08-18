package service;

import DAO.EventDAO;
import DAO.FileDAO;
import DAO.UserDAO;
import model.Event;
import model.File;
import model.User;
import java.util.List;

public class EventService {

    private EventDAO eventDAO = new EventDAO();
    private UserDAO userDAO = new UserDAO();
    private FileDAO fileDAO = new FileDAO();

    public void createEvent(Integer userId, Integer fileId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("id пользователя должен быть положительным числом");
        }

        if (fileId == null || fileId <= 0) {
            throw new IllegalArgumentException("id файла должен быть положительным числом");
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("пользователь с id " + userId + " не найден");
        }

        File file = fileDAO.findById(fileId);
        if (file == null) {
            throw new RuntimeException("файл с id " + fileId + " не найден");
        }

        Event event = new Event(user, file);
        eventDAO.save(event);
        System.out.println("service: событие создано (пользователь " + userId + " скачал файл " + fileId + ")");
    }

    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    public Event getEventById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new RuntimeException("событие с id " + id + " не найдено");
        }

        event.getUser().getName();
        event.getFile().getName();

        return event;
    }

    public List<Event> getEventsByUser(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("id пользователя должен быть положительным числом");
        }

        if (userDAO.findById(userId) == null) {
            throw new RuntimeException("пользователь с id " + userId + " не найден");
        }

        return eventDAO.findByUserId(userId);
    }

    public List<Event> getEventsByFile(Integer fileId) {
        if (fileId == null || fileId <= 0) {
            throw new IllegalArgumentException("id файла должен быть положительным числом");
        }

        if (fileDAO.findById(fileId) == null) {
            throw new RuntimeException("файл с id " + fileId + " не найден");
        }

        return eventDAO.findByFileId(fileId);
    }

    public List<Event> getEventsByUserAndFile(Integer userId, Integer fileId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("id пользователя должен быть положительным числом");
        }
        if (fileId == null || fileId <= 0) {
            throw new IllegalArgumentException("id файла должен быть положительным числом");
        }

        return eventDAO.findByUserAndFile(userId, fileId);
    }

    public void updateEvent(Integer id, Integer newUserId, Integer newFileId) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        Event event = getEventById(id);

        if (newUserId != null && newUserId > 0) {
            User user = userDAO.findById(newUserId);
            if (user == null) {
                throw new RuntimeException("пользователь с id " + newUserId + " не найден");
            }
            event.setUser(user);
        }

        if (newFileId != null && newFileId > 0) {
            File file = fileDAO.findById(newFileId);
            if (file == null) {
                throw new RuntimeException("файл с id " + newFileId + " не найден");
            }
            event.setFile(file);
        }

        eventDAO.update(event);
        System.out.println("service: событие обновлено");
    }

    public void deleteEvent(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new RuntimeException("cобытие с id " + id + " не найдено");
        }

        eventDAO.delete(id);
        System.out.println("service: событие удалено");
    }

    public long countEvents() {
        return eventDAO.findAll().size();
    }

    public long countEventsByUser(Integer userId) {
        return eventDAO.findByUserId(userId).size();
    }

    public long countEventsByFile(Integer fileId) {
        return eventDAO.findByFileId(fileId).size();
    }
}

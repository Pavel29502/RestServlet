package service;

import model.User;
import DAO.UserDAO;
import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public void createUser(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя не может быть пустым");
        }

        User existing = userDAO.findByName(name);
        if (existing != null) {
            throw new RuntimeException("пользователь с именем " + name + " уже существует");
        }

        User user = new User(name);
        userDAO.save(user);
        System.out.println("service: пользователь создан");
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        User user = userDAO.findById(id);
        if (user == null) {
            throw new RuntimeException("пользователь с id " + id + " не найден");
        }

        user.getEvents().size();

        return user;
    }

    public User getUserByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя не может быть пустым");
        }

        User user = userDAO.findByName(name);
        if (user == null) {
            throw new RuntimeException("пользователь с именем " + name + " не найден");
        }

        return user;
    }

    public void updateUser(Integer id, String newName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("имя не может быть пустым");
        }

        User user = getUserById(id);

        User existing = userDAO.findByName(newName);
        if (existing != null && !existing.getId().equals(id)) {
            throw new RuntimeException("пользователь с именем " + newName + " уже существует");
        }

        user.setName(newName);
        userDAO.update(user);
        System.out.println("service: пользователь обновлен");
    }

    public void deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        User user = userDAO.findById(id);
        if (user == null) {
            throw new RuntimeException("пользователь с id " + id + " не найден");
        }

        userDAO.delete(id);
        System.out.println("service: пользователь удален");
    }

    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return userDAO.findById(id) != null;
    }

    public long countUsers() {
        return userDAO.findAll().size();
    }

}

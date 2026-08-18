package service;

import DAO.FileDAO;
import model.File;
import java.util.List;

public class FileService {
    private FileDAO fileDAO = new FileDAO();

    public void createFile(String name, String filePath) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя файла не может быть пустым");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу не может быть пустым");
        }

        File existing = fileDAO.findByFilePath(filePath);
        if (existing != null) {
            throw new RuntimeException("файл с путем " + filePath + " уже существует");
        }

        File file = new File(name, filePath);
        fileDAO.save(file);
        System.out.println("файл создан");

    }
        public List<File> getAllFiles() {
            return fileDAO.findAll();
    }


    public File getFileById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным");
        }

        File file = fileDAO.findById(id);
        if (file == null) {
            throw new RuntimeException("файл c id " + id + " не найден");
        }

        file.getEvents().size();

        return file;
    }

    public File getFileByPath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("путь не может быть пустым");
        }

        File file = fileDAO.findByFilePath(filePath);
        if (file == null) {
            throw new RuntimeException("файл с путем " + filePath + " не найден");
        }
        return file;
    }

    public List<File> getFilesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("имя не может быть пустым");
        }
        return fileDAO.findByName(name);
    }

    public void updateFile(Integer id, String newName, String newFilePath) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным");
        }

        File file = getFileById(id);

        if (newName != null && !newName.trim().isEmpty()) {
            file.setName(newName);
        }

        if (newFilePath != null && !newFilePath.trim().isEmpty()) {
            File existing = fileDAO.findByFilePath(newFilePath);
            if (existing != null && !existing.getId().equals(id)) {
                throw new RuntimeException("файл с путем " + newFilePath + " уже существует");
            }
            file.setFilePath(newFilePath);
        }
        fileDAO.update(file);
        System.out.println("service: файл обновлен");
    }

    public void deleteFile(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть положительным числом");
        }

        File file = fileDAO.findById(id);
        if (file == null) {
            throw new RuntimeException("файл с id " + id + " не найден");
        }

        fileDAO.delete(id);
        System.out.println("service: файл удален");
    }

    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return fileDAO.findById(id) != null;
    }
}

package DAO;

import Hibernate.HibernateUtil;
import model.File;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class FileDAO {

    public void save(File file) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(file);
            transaction.commit();
            System.out.println("file сохранен" + file.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public File findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            File file = session.get(File.class, id);
            if (file != null) {
                file.getEvents().size();
            }
            return file;
        }
    }

    public List<File> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<File> query = session.createQuery(
                    "SELECT DISTINCT f FROM File f LEFT JOIN FETCH f.events", File.class);
            return query.list();
        }
    }

        public File findByFilePath(String filePath) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<File> query = session.createQuery("FROM File WHERE filePath = :filePath", File.class);
                query.setParameter("filePath", filePath);
                return query.uniqueResult();
            }
        }

        public List<File> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<File> query = session.createQuery("FROM File WHERE name = :name", File.class);
            query.setParameter("name", name);
            return query.list();
        }
        }

        public void update(File file) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(file);
            transaction.commit();
            System.out.println("file обновлен" + file.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        }

        public void delete(Integer id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            File file = session.get(File.class, id);
            if (file != null) {
                session.remove(file);
                transaction.commit();
                System.out.println("file удален" + file.getName());
            } else {
                System.out.println("file s id " + id + " не найден");
                transaction.rollback();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        }

     }


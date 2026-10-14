package DAO;

import Hibernate.HibernateUtil;
import model.Event;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class EventDAO {

    public void save(Event event) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(event);
            transaction.commit();
            System.out.println("event сохранен");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка сохранения события: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

        public Event findById(Integer id) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Event> query = session.createQuery(
                        "SELECT DISTINCT e FROM Event e " +
                                "LEFT JOIN FETCH e.user " +
                                "LEFT JOIN FETCH e.file " +
                                "WHERE e.id = :id", Event.class);
                query.setParameter("id", id);
                return query.uniqueResult();
            }
        }

    public List<Event> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery(
                    "SELECT DISTINCT e FROM Event e " +
                            "LEFT JOIN FETCH e.user " +
                            "LEFT JOIN FETCH e.file", Event.class);
            return query.list();
        }
    }

    public List<Event> findByUserId(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery("SELECT DISTINCT e FROM Event e " +
                    "LEFT JOIN FETCH e.user " +
                    "LEFT JOIN FETCH e.file " +
                    "WHERE e.user.id = :userId", Event.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }

        public List<Event> findByFileId(Integer fileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery(
                    "SELECT DISTINCT e FROM Event e " +
                            "LEFT JOIN FETCH e.user " +
                            "LEFT JOIN FETCH e.file " +
                            "WHERE e.file.id = :fileId", Event.class);
            query.setParameter("fileId", fileId);
            return query.list();
        }
        }

        public List<Event> findByUserAndFile(Integer userId, Integer fileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery(
                    "SELECT DISTINCT e FROM Event e " +
                            "LEFT JOIN FETCH e.user " +
                            "LEFT JOIN FETCH e.file " +
                            "WHERE e.user.id = :userId AND e.file.id = :fileId", Event.class);
            query.setParameter("userId", userId);
            query.setParameter("fileId", fileId);
            return query.list();
        }
        }


    public void update(Event event) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(event);
            transaction.commit();
            System.out.println("событие обновлено");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка обновления события: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void delete(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Event event = session.get(Event.class, id);

            if (event != null) {
                session.remove(event);
                transaction.commit();
                System.out.println("событие удалено");
            } else {
                transaction.rollback();
                throw new RuntimeException("Событие с id " + id + " не найдено");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Ошибка удаления события: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    }


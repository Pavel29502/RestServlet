package DAO;

import Hibernate.HibernateUtil;
import model.Event;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class EventDAO {

    public void save(Event event) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(event);
            transaction.commit();
            System.out.println("event сохранен");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

        public Event findById(Integer id) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Event event = session.get(Event.class, id);
                if (event != null) {
                    event.getUser().getName();
                    event.getFile().getName();
                }
                return event;
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
            Query<Event> query = session.createQuery(
                    "FROM Event WHERE user.id = :userId", Event.class);
            query.setParameter("userId", userId);
            return query.list();
        }
        }

        public List<Event> findByFileId(Integer fileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery(
                    "FROM Event WHERE file.id = :fileId", Event.class);
            query.setParameter("fileId", fileId);
            return query.list();
        }
        }

        public List<Event> findByUserAndFile(Integer userId, Integer fileId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Event> query = session.createQuery(
                    "FROM Event WHERE user.id = :userId AND file.id = :fileId", Event.class);
            query.setParameter("userId", userId);
            query.setParameter("fileId", fileId);
            return query.list();
        }
        }

        public void update(Event event) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(event);
            transaction.commit();
            System.out.println("событие обновлено");
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
            Event event = session.get(Event.class, id);
            if (event != null) {
                session.remove(event);
                transaction.commit();
                System.out.println("событие удалено");
            } else {
                System.out.println(" событие с id " + id + " не найдено");
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


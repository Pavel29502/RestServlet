package DAO;

import Hibernate.HibernateUtil;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class UserDAO {

        public void save(User user) {
            Session session = null;
            Transaction transaction = null;

            try {
                session = HibernateUtil.getSessionFactory().openSession();
                transaction = session.beginTransaction();
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Ошибка сохранения пользователя "  +
                        e.getMessage(), e);
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.events", User.class);
            return query.list();
        }
    }

    public User findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User user = session.get(User.class, id);
            if (user != null) {
                user.getEvents().size();
            }
            return user;
        }
    }

    public User findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("from User where name = :name", User.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        }
    }

    public void update(User user) {
            Session session = null;
            Transaction transaction = null;

            try {
                session = HibernateUtil.getSessionFactory().openSession();
                transaction = session.beginTransaction();
                session.merge(user);
                transaction.commit();
                System.out.println("Пользователь обновлен " + user.getName());
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Ошибка обновления пользователя: " + e.getMessage(), e);
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
                User user = session.get(User.class, id);

                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                    System.out.println("Пользователь удален " + user.getName());
                } else {
                    transaction.rollback();
                    throw new RuntimeException("Пользователь с id " + id + " не найден");
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Ошибка удаления пользователя: " + e.getMessage(), e);
            } finally {
                if (session != null) {
                    session.close();
                }
            }
    }
}

package Hibernate;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(
                            "jdbc:mysql://localhost:3307/servlet?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                            "root",
                            "root"
                    )
                    .load();
            flyway.migrate();

            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (getSessionFactory() != null) {
            getSessionFactory().close();
        }
    }
}

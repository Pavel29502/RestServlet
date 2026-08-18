package Hibernate;

public class TestHiber {
    public static void main(String[] args) {
        System.out.println("SessionFactory создана " + HibernateUtil.getSessionFactory());
        HibernateUtil.shutdown();
    }
}

package Database;

import Entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfigurator {

    private static SessionFactory factory;

    private static void createSessionFactory() {

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Case.class);
        configuration.addAnnotatedClass(CaseCooler.class);
        configuration.addAnnotatedClass(Component.class);
        configuration.addAnnotatedClass(CoolingSystem.class);
        configuration.addAnnotatedClass(CPU.class);
        configuration.addAnnotatedClass(GPU.class);
        configuration.addAnnotatedClass(Motherboard.class);
        configuration.addAnnotatedClass(PowerUnit.class);
        configuration.addAnnotatedClass(PrimaryStorage.class);
        configuration.addAnnotatedClass(RAM.class);
        configuration.addAnnotatedClass(Computer.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.configure();

        factory = configuration.buildSessionFactory();

    }

    public static Session getSession() {
        if (factory == null) {
            createSessionFactory();
        }
        return factory.openSession();
    }

}



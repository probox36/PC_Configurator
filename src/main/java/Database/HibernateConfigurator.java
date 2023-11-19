package Database;

import Components.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfigurator {

    public static Session getSession() {

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
        configuration.configure();

        SessionFactory factory = configuration.buildSessionFactory();
        return factory.openSession();

    }

}



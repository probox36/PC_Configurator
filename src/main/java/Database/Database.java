package Database;

import Entities.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.ArrayList;

public class Database {

    private final Session session;

    public Database() {
        session = HibernateConfigurator.getSession();
    }

    public <T> ArrayList<T> performQuery(String hql, Class<T> tClass) {
        Query<T> query = session.createQuery(hql, tClass);
        return new ArrayList<>(query.list());
    }

    public <T extends Component> ArrayList<Component> getComponents(Class<T> tClass) {
        String hql = "from " + tClass.getName();
        return performQuery(hql, Component.class);
    }

    public ArrayList<Component> getComponents(String componentType) {
        String hql = "from " + componentType;
        return performQuery(hql, Component.class);
    }

}

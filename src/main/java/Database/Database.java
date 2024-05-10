package Database;

import Entities.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

    public void persist(DBEntity entity) {
        Transaction t = session.beginTransaction();
        session.persist(entity);

        t.commit();
    }

    public <T extends DBEntity> ArrayList<T> getByFieldValue(Class<T> tClass, String field, String value) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> critQuery = builder.createQuery(tClass);
        Root<T> root = critQuery.from(tClass);

        critQuery.select(root).where(builder.equal(root.get(field), value));
        Query<T> query = session.createQuery(critQuery);
        return (ArrayList<T>) query.list();
    }

}

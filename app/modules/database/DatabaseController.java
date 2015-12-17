package modules.database;

import modules.database.entities.DomainObject;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

/**
 * für alle krassen Party leute: http://stackoverflow.com/questions/24572092/using-java-generics-for-jpa-findall-query-with-where-clause
 * Created by FRudi on 26.11.2015.
 */
public abstract class DatabaseController<T extends DomainObject, T2> {

    @Transactional
    public <T> T selectEntity(Class<T> type, String whereColumn, String where){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<T> rc = builder.createQuery(type);
        Root<T> rootQuery = rc.from(type);

        rc.where(builder.equal(rootQuery.get(whereColumn), where));

        TypedQuery<T> query = JPA.em().createQuery(rc);

        List<T> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        if(list.size() > 1){
            return list.get(0);
        }

        return query.getSingleResult();
    }

    public <T> List<T> selectEntityList(Class<T> type, String whereColumn, Object where){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<T> rc = builder.createQuery(type);
        Root<T> rootQuery = rc.from(type);

        rc.where(builder.equal(rootQuery.get(whereColumn), where));

        TypedQuery<T> query = JPA.em().createQuery(rc);

        List<T> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        return list;
    }

    @Transactional
    public <T> T selectEntity(Class<T> type, T2 where){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<T> rc = builder.createQuery(type);

        createWhere(builder, type, rc, where);
        TypedQuery<T> query = JPA.em().createQuery(rc);

        List<T> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        if(list.size() > 1){
            return list.get(0);
        }

        return query.getSingleResult();
    }

    @Transactional
    public <T> List<T> selectEntityList(Class<T> type, T2 where){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<T> rc = builder.createQuery(type);

        createWhere(builder, type, rc, where);
        TypedQuery<T> query = JPA.em().createQuery(rc);

        List<T> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        return list;
    }

    private <T> void createWhere(CriteriaBuilder builder, Class<T> type, CriteriaQuery<T> rc, T2 where) {
        Root<T> rootQuery = rc.from(type);
        if (where != null) {
            EntityType<T> entity = JPA.em().getMetamodel().entity(type);
            SingularAttribute<? super T, ?> attribute = null;
            for (SingularAttribute<? super T, ?> singleAttribute: entity.getSingularAttributes()) {
                // loop through all attributes that match this class
                if (singleAttribute.getJavaType().equals(where.getClass())) {
                    // winner!
                    attribute = singleAttribute;
                    break;
                }
            }
            // where t.object = object.getID()
            rc.where(builder.equal(rootQuery.get(attribute), where));
        }
        rc.select(rootQuery);
    }

    @Transactional
    public void persistObject(Object persistObject){
        JPA.em().persist(persistObject);
    }

}

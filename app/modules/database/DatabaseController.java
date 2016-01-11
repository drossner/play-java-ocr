package modules.database;

import modules.database.entities.DomainObject;
import modules.database.entities.LayoutConfig;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * für alle krassen Party leute: http://stackoverflow.com/questions/24572092/using-java-generics-for-jpa-findall-query-with-where-clause
 * Created by FRudi on 26.11.2015.
 */
public abstract class DatabaseController<T extends DomainObject, T2> {

    /**
     * selektiert aus der datenbank eine entität aus der tabelle des übergebenen typs und schränkt die ergebnisse auf den übergebenen wert der where cause ein
     * falls mehr als eine entität selektiert wird, wird das erste objekt der liste zurückgegeben
     * @param type typ der entität die selektiert werden soll
     * @param whereColumn spalte der einschränkungs bedingung
     * @param where wert der einschränkung bedingung
     * @param <T> typ der entität die selektiert werden soll
     * @return
     */
    @Transactional
    public <T> T selectEntity(Class<T> type, String whereColumn, Object where){
        Logger.info("selecting: " + type + " column: " + whereColumn + " value: " + where);
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

    /**
     * selektiert eine liste an entitäten des übergebenen typs aus der datenbank
     * die ergebnismenge wird über die übergebenen spalten und die dazugehörigen werte eingeschränkt
     * whereColumn und where müssen die selbe länge haben und zusammengehörige paare aus spalte und wert müssen den selben index in den listen haben
     * @param type typ der entität die selektiert werden soll
     * @param whereColumn liste an spalten für die einschränkung der ergebnismenge
     * @param where liste an werten für die einschränkung der ergebnismenge
     * @param <T> typ der entität die selektiert werden soll
     * @return liste der selektierten entitäten
     */
    public <T> List<T> selectEntityList(Class<T> type, List<String> whereColumn, List<Object> where){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<T> rc = builder.createQuery(type);
        Root<T> rootQuery = rc.from(type);

        List<Predicate> predicates = new ArrayList<>();

        for (int i = 0; i < whereColumn.size(); i++) {
            predicates.add(builder.equal(rootQuery.get(whereColumn.get(i)), where.get(i)));
        }
        rc.where(predicates.toArray(new Predicate[predicates.size()]));

        TypedQuery<T> query = JPA.em().createQuery(rc);

        List<T> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        return list;
    }

    /**
     * selektiert eine liste an entitäten des übergebenen typs aus der datenbank
     * die ergebnismenge wird dabei über die übergebene spalte und den wert eingeschränkt
     * wenn die anzahl der zurückgegebenen entitäten größer 1 ist, wird die erste entität der liste zurückgegeben
     * @param type typ der entität die selektiert werden soll
     * @param whereColumn spalte für die einschränkung der ergebnismenge
     * @param where wert für die einschränkung der ergebnismenge
     * @param <T> typ der entität die selektiert werden soll
     * @return selektierte entität
     */
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

    /**
     * selektiert eine entität aus der datenbank mit dem übergebenen typen und schränkt dabei die erbgenismenge durch das übergebenene where ein
     * dabei wird über den typ des where, die korrekte spalte ermittelt
     * wenn die entität mehrmals den selben datentyp als variable hat, ist diese methode nicht eindeutig
     * wenn die ergebnismenge größer 1 ist, wird die erste entität der liste zurückgegeben
     * @param type typ der entität die selektiert werden soll
     * @param where wert für die einschränkung der ergebnismenge
     * @param <T> typ der entität die selektiert werden soll
     * @return selektierte entität
     */
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

    /**
     * selektiert eine liste an entität aus der datenbank mit dem übergebenen typen und schränkt dabei die erbgenismenge durch das übergebenene where ein
     * dabei wird über den typ des where, die korrekte spalte ermittelt
     * wenn die entität mehrmals den selben datentyp als variable hat, ist diese methode nicht eindeutig
     * wenn die ergebnismenge größer 1 ist, wird die erste entität der liste zurückgegeben
     * @param type typ der entität die selektiert werden soll
     * @param where wert für die einschränkung der ergebnismenge
     * @param <T> typ der entität die selektiert werden soll
     * @return selektierte liste an entitäten
     */
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

    /**
     * fügt der sql anfrage generisch die übergebene einschränkung hinzu
     * ist bei einer entität, die mehrmals den typ der einschränkung als variablentyp hat nicht eindeutig
     * @param builder sql abfragen builder
     * @param type typ der entität
     * @param rc sql anfrage
     * @param where einschränkungswert
     * @param <T> typ der entität
     */
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

    /**
     * speichert das übergebene objekt in der datenbank
     * @param persistObject abzuspeicherndes objekt
     */
    @Transactional
    public void persistObject(DomainObject persistObject){
        JPA.em().persist(persistObject);
    }

    /**
     * löscht das übergebene objekt aus der datenbank
     * @param persistObject zu löschendes objekt
     */
    public void deleteObject(DomainObject persistObject){
        JPA.em().remove(persistObject);
    }

}

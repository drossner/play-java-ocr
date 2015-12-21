package modules.database;

import modules.database.entities.DomainObject;
import modules.database.entities.LayoutConfig;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

/**
 * Created by florian on 18.12.15.
 */
public class LayoutConfigurationController extends DatabaseController<LayoutConfig, Object> {

    @Override
    @Transactional
    public void persistObject(DomainObject persistObject){
        super.persistObject(persistObject);
    }


    public List<LayoutConfig> selectEntityListColumnNull(String whereColumn){
        CriteriaBuilder builder = JPA.em().getCriteriaBuilder();

        CriteriaQuery<LayoutConfig> rc = builder.createQuery(LayoutConfig.class);
        Root<LayoutConfig> rootQuery = rc.from(LayoutConfig.class);

        rc.where(builder.isNull(rootQuery.get(whereColumn)));

        TypedQuery<LayoutConfig> query = JPA.em().createQuery(rc);

        List<LayoutConfig> list = query.getResultList();

        if(list.size() == 0){
            return null;
        }

        return list;
    }
}

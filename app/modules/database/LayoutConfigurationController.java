package modules.database;

import modules.database.entities.DomainObject;
import modules.database.entities.LayoutConfig;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Created by florian on 18.12.15.
 */
public class LayoutConfigurationController extends DatabaseController<LayoutConfig, Object> {

    @Override
    @Transactional
    public void persistObject(DomainObject persistObject){
        super.persistObject(persistObject);
    }
}

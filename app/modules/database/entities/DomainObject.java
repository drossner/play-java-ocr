package modules.database.entities;

import java.io.Serializable;

/**
 * Created by FRudi on 28.11.2015.
 */
public abstract class DomainObject implements Serializable {

    public abstract void setId(int id);
    public abstract int getId();

}
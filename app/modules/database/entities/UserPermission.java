package modules.database.entities;

import be.objectify.deadbolt.core.models.Permission;
import controllers.security.OcrPermission;

import javax.persistence.*;

/**
 * Created by daniel on 23.11.15.
 */
@Entity(name="UserPermission")
@Table(name="UserPermission")
/*
        @NamedQuery(
                name = "findUserPermissionByName",
                query = "from UserPermission s where s.name = :name"
        )
*/
public class UserPermission extends DomainObject implements Permission
{
    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_value")
    private OcrPermission value;

    public String getValue()
    {
        return value.getValue();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setValue(OcrPermission value) {
        this.value = value;
    }
}

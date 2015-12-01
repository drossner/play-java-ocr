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
public class UserPermission implements Permission
{
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_value")
    private OcrPermission value;

    public String getValue()
    {
        return value.getValue();
    }

}

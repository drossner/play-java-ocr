package modules.database.entities;

import be.objectify.deadbolt.core.models.Permission;

import javax.persistence.*;

/**
 * Created by daniel on 23.11.15.
 */
@NamedQueries({
        @NamedQuery(
                name = "findUserPermissionByName",
                query = "from UserPermission s where s.name = :name"
        )
})

@Entity(name="UserPermission")
@Table(name="UserPermission")
public class UserPermission implements Permission
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_value")
    private String value;

    public String getValue()
    {
        return value;
    }

}

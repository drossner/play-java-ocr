package modules.database.entities;

import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrRole;

import javax.persistence.*;

/**
 * Created by daniel on 23.11.15.
 */
@Entity(name="SecurityRole")
@Table(name="SecurityRole")
/*
        @NamedQuery(
                name = "findSecurityRoleByName",
                query = "from SecurityRole s where s.name = :name"
        )
*/
public class SecurityRole implements Role
{
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private OcrRole name;

    public String getName()
    {
        return name.getName();
    }

}
package modules.database.entities;

import be.objectify.deadbolt.core.models.Role;

import javax.persistence.*;

/**
 * Created by daniel on 23.11.15.
 */
@NamedQueries({
        @NamedQuery(
                name = "findSecurityRoleByName",
                query = "from SecurityRole s where s.name = :name"
        )
})

@Entity
@Table(name="SecurityRole")
public class SecurityRole implements Role
{
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    public String getName()
    {
        return name;
    }

}
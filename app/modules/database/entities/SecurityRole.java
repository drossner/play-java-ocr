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
public class SecurityRole extends DomainObject implements Role
{
    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    @Column
    private OcrRole name;

    public SecurityRole(OcrRole role){
        name = role;
    }

    public String getName()
    {
        return name.getName();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setName(OcrRole name) {
        this.name = name;
    }
}
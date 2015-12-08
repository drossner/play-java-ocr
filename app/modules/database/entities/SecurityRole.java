package modules.database.entities;

import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrRole;

import javax.persistence.*;

/**
 * Created by daniel on 23.11.15.
 */
@Entity(name="SecurityRole")
@Table(name="SecurityRole" , uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
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

    public SecurityRole(){
        name = OcrRole.DEFAULT;
    }

    public SecurityRole(OcrRole role){
        name = role;
    }

    public SecurityRole(){

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
package modules.database.entities;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import controllers.security.OcrRole;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="Users")
public class User implements Subject {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String eMail;

    @OneToOne(cascade = {CascadeType.ALL})
    private Country country;

    @Column
    private String password;

    @Column
    private String cmsAccount;

    @Column
    private String cmsPassword;

    @ManyToMany
    private List<SecurityRole> roles = new LinkedList<SecurityRole>();

    @ManyToMany
    private List<UserPermission> permission = new LinkedList<UserPermission>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCmsAccount() {
        return cmsAccount;
    }

    public void setCmsAccount(String cmsAccount) {
        this.cmsAccount = cmsAccount;
    }

    public String getCmsPassword() {
        return cmsPassword;
    }

    public void setCmsPassword(String cmsPassword) {
        this.cmsPassword = cmsPassword;
    }

    public void addRole(OcrRole role){
        roles.add(new SecurityRole(role));
    }

    @Override
    public List<? extends Role> getRoles() {
        return roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return permission;
    }

    @Override
    public String getIdentifier() {
        return geteMail();
    }

}

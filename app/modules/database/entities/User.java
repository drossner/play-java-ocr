package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="User")
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String eMail;

    @Column
    private Country country;

    @Column
    private String password;

    @Column
    private String cmsAccount;

    @Column
    private String cmsPassword;

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
}

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

}

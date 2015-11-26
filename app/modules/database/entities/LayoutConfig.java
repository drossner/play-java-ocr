package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="LayoutConfig")
public class LayoutConfig extends DomainObject{

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private User user;

    @OneToOne
    private Country language;

    @Column
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Country getLanguage() {
        return language;
    }

    public void setLanguage(Country language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

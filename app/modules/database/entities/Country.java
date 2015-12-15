package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */

@Entity
@Table(name="Country", uniqueConstraints = {@UniqueConstraint(columnNames = "country")})
public class Country extends DomainObject{

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private CountryImpl country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CountryImpl getCountry() {
        return country;
    }

    public void setCountry(CountryImpl country) {
        this.country = country;
    }
}

package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */

@Entity
@Table(name="Country")
public class Country extends DomainObject{

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

    @Column
    private int isoCode;

    public Country(String name, int isoCode){
        setName(name);
        setIsoCode(isoCode);
    }

    public Country() {

    }

    public int getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(int isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

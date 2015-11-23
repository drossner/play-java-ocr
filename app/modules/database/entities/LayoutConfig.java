package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="LayoutConfig")
public class LayoutConfig {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private User user;

    @Column
    private Country language;

    @Column
    private String name;
}
package modules.database.entities;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="LayoutFragment")
public class LayoutFragment {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private LayoutConfig layoutConfig;

    @Column
    private double xStart;

    @Column
    private double yStart;

    @Column
    private double xEnd;

    @Column
    private double yEnd;

    @Column
    private String type;
}

package modules.database.entities;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="Image")
public class Image {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private Blob source;

    @Column
    private String solution;

    @Column
    private String format;

    @Column
    private Date createDate;

    @Column
    private String orientation;

    @Column
    private double focalLength;

    @Column
    private double isoValue;

    @Column
    private double longitude;

    @Column
    private double latitude;
}

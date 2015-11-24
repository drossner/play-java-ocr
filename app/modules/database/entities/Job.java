package modules.database.entities;

import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="Job")
public class Job {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

    @Column
    @OneToOne
    private User user;

    @Column
    private DateTime startTime;

    @Column
    private DateTime endTime;

    @Column
    private String category;

    @Column
    private String notification;

    @Column
    private boolean notificationSend;

    @Column
    @OneToOne
    private LayoutConfig layoutConfig;

    @Column
    private String resultFile;

}

package modules.database.entities;

import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="Job")
public class Job extends DomainObject{

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

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
    private boolean processed;

    @OneToOne
    private LayoutConfig layoutConfig;

    @OneToOne
    private Image image;

    @Column
    private String resultFile;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public LayoutConfig getLayoutConfig() {
        return layoutConfig;
    }

    public void setLayoutConfig(LayoutConfig layoutConfig) {
        this.layoutConfig = layoutConfig;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }
}

package modules.database.entities;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

/**
 * Created by florian on 17.11.15.
 */
@Entity
@Table(name="Image")
public class Image extends DomainObject{

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String source;

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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public double getIsoValue() {
        return isoValue;
    }

    public void setIsoValue(double isoValue) {
        this.isoValue = isoValue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

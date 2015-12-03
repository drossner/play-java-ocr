package modules.database.factory;

import modules.database.entities.Image;
import org.joda.time.DateTime;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by florian on 02.12.15.
 */
public class SimpleImageFactory {

    private Image image = new Image();

    public Image build(){
        return image;
    }

    public SimpleImageFactory setSource(byte[] source) {
        try {
            image.setSource(new SerialBlob(source));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public SimpleImageFactory setSolution(String solution) {
        image.setSolution(solution);

        return this;
    }

    public SimpleImageFactory setFormat(String format) {
        image.setFormat(format);

        return this;
    }

    public SimpleImageFactory setCreateDate(DateTime createDate) {
        image.setCreateDate(createDate.toDate());

        return this;
    }

    public SimpleImageFactory setOrientation(String orientation) {
        image.setOrientation(orientation);

        return this;
    }

    public SimpleImageFactory setFocalLength(double focalLength) {
        image.setFocalLength(focalLength);
        return this;

    }

    public SimpleImageFactory setIsoValue(double isoValue) {
        image.setIsoValue(isoValue);
        return this;
    }

    public SimpleImageFactory setLongitude(double longitude) {
        image.setLongitude(longitude);
        return this;
    }

    public SimpleImageFactory setLatitude(double latitude) {
        image.setLatitude(latitude);
        return this;
    }
}

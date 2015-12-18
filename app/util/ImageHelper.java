package util;

import com.google.inject.ImplementedBy;
import modules.upload.FileContainer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by daniel on 18.12.15.
 */
@ImplementedBy(ImageHelperImpl.class)
public interface ImageHelper {

    int THUMBNAIL_WIDTH = 80;
    int THUMBNAIL_HEIGHT = 80;

    int TEMPLATE_WIDTH = 536;

    String OUTPUT_FORMAT = "jpeg";
    String OUTPUT_MIMETYPE = "image/jpeg";

    BufferedImage convertFile(FileContainer inputFile) throws IOException;

    ByteArrayOutputStream convertBaos(BufferedImage image) throws IOException;

    ByteArrayOutputStream convertBaos(File file) throws IOException;

    boolean fileIsValid(FileContainer fc);

    boolean fileIsValid(File f);

    BufferedImage scale(BufferedImage image, int width, int heigth);

    BufferedImage getThumbnail(BufferedImage image);

}

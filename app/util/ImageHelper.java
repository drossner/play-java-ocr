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

    /**
     * Extracts a BufferedImage out of a FileContainer.
     * @param inputFile FileContainer
     * @return BufferedImage object
     * @throws IOException
     */
    BufferedImage convertFile(FileContainer inputFile) throws IOException;

    /**
     * Convert a BufferedImage to a ByteArrayOutputStream
     * @param image
     * @throws IOException
     */
    ByteArrayOutputStream convertBaos(BufferedImage image) throws IOException;

    /**
     * Convert a {@see File} to a ByteArrayOutputStream
     * @param file
     * @throws IOException
     */
    ByteArrayOutputStream convertBaos(File file) throws IOException;

    /**
     * Validates if the FileContainer contains a valid image file.
     * @param fc FileContainer
     * @return true = valid
     */
    boolean fileIsValid(FileContainer fc);

    /**
     * Validates if the file is a valid image file
     * @param f File
     * @return true = valid
     */
    boolean fileIsValid(File f);

    /**
     * Scales a BufferedImage to the specified size.
     * @param image input image
     * @param width width in px
     * @param heigth height in px
     * @return scaled image
     */
    BufferedImage scale(BufferedImage image, int width, int heigth);

    /**
     * Scales a BufferedImage to thumbnail-size (specified in constants)
     * @param image input image
     * @return scaled image
     */
    BufferedImage getThumbnail(BufferedImage image);

}

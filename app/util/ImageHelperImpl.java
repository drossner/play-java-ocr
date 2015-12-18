package util;

import modules.upload.FileContainer;

import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.entities.Image;
import org.imgscalr.Scalr;
import play.Logger;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by daniel on 05.12.15.
 */
@Singleton
public class ImageHelperImpl implements ImageHelper{

    private HashSet<String> ioExtensions;

    public ImageHelperImpl(){
        ImageIO.scanForPlugins();
        ioExtensions = new HashSet<>();
        String[] extensionArray = ImageIO.getReaderFileSuffixes();
        Logger.info("ImageHelper init  - Supported extensions: " + Arrays.toString(extensionArray));
        for(String s : extensionArray){
            ioExtensions.add(s.toLowerCase());
        }
    }

    public BufferedImage convertFile(FileContainer inputFile) throws IOException {
        File f = inputFile.getFile();
        String fileType = getFileType(inputFile);
        //ImageIO does the job
        if(ioExtensions.contains(fileType)){
            BufferedImage image = ImageIO.read(f);
            return fixTransparency(image);
        } else throw new IOException("Unsupported file-type");
    }

    private BufferedImage fixTransparency(BufferedImage image){
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        //fill transparent pixels with white
        img.getGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        return img;
    }

    public ByteArrayOutputStream convertBaos(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, OUTPUT_FORMAT, baos);
        return baos;
    }

    public ByteArrayOutputStream convertBaos(File file) throws IOException{
        //File is unchecked!
        BufferedImage image = ImageIO.read(file);
        return convertBaos(image);
    }

    public boolean fileIsValid(FileContainer fc){
        boolean readable = false;
        String fileType = getFileType(fc);
        readable = ioExtensions.contains(fileType);
        if(!readable) return readable;
        else {
            return fileIsValid(fc.getFile());
        }
    }

    public boolean fileIsValid(File f){
        boolean readable = false;
        try {
            readable = ImageIO.read(f) != null;
        } catch (IOException e) {
            Logger.debug(e.getMessage(), e);
        }
        return readable;

    }

    private String getFileType(FileContainer fc){
        String contentType = fc.getContentType();
        String[] splittedType = contentType.split("/");
        if(splittedType.length != 2) return "";
        return splittedType[1].toLowerCase();
    }

    public BufferedImage scale(BufferedImage image, int width, int height){
        BufferedImage thumbnail =
                Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);
        return fixTransparency(thumbnail);
    }

    public BufferedImage getThumbnail(BufferedImage image){
        BufferedImage thumbnail =
                Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT,
                        THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        return fixTransparency(thumbnail);
    }

    public BufferedImage convertToImageFromCMIS(String source) {
        CMSController controller = SessionHolder.getInstance().getController("ocr", "ocr");

        return controller.readingAImage(source);
    }
}

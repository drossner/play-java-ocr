package modules.upload;


import modules.database.entities.Image;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by daniel on 05.12.15.
 */
public class ImageHelper {

    private HashSet<String> ioExtensions;

    public ImageHelper(){
        ImageIO.scanForPlugins();
        ioExtensions = new HashSet<>();
        String[] extensionArray = ImageIO.getReaderFileSuffixes();
        Logger.info("ImageHelper init  - Supported extenstions: " + Arrays.toString(extensionArray));
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
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            //fill transparent pixels with white
            img.getGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            return img;
        } else throw new IOException("Unsupported file-type");
    }

    public boolean fileIsValid(FileContainer fc){
        boolean readable = false;
        try {
            readable = ImageIO.read(fc.getFile()) != null;
        } catch (IOException e) {
            Logger.debug(e.getMessage(), e);
        }
        return readable;
    }

    private String getFileType(FileContainer fc){
        String contentType = fc.getContentType();
        String[] splittedType = contentType.split("/");
        assert(splittedType.length == 2);
        return splittedType[1].toLowerCase();
    }

}

package modules.upload;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import controllers.routes;
import org.imgscalr.Scalr;
import play.Logger;
import play.Play;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Daniel on 28.11.2015.
 */
@Singleton
public class UploadHandler {

    private final long MAXIMAL_CACHE_TIME = 1000*60*60; //1 Stunde
    private final int THUMBNAIL_WIDTH = 80;
    private final int THUMBNAIL_HEIGTH = 80;
    private final String TEMP_DIR_NAME = "/uploadTemp/";
    private final File TEMP_DIR;

    /*@Inject
    @NamedCache("upload-cache")
    private CacheApi cache;*/
    private ConcurrentSkipListMap<String, Long> uploadIds;
    private ConcurrentSkipListMap<String, CopyOnWriteArrayList<File>> fileList;

    public UploadHandler() throws IOException {
        this.uploadIds = new ConcurrentSkipListMap<String, Long>();
        this.fileList = new ConcurrentSkipListMap<>();

        //Create custom temp dir (fuck that bug!!!!11111elf)
        //Create custom temp dir (fuck that bug!!!!11111elf)
        TEMP_DIR = new File(Play.application().path().getAbsolutePath() + TEMP_DIR_NAME);
        TEMP_DIR.mkdir();
    }

    /**
     * Creates an unique id to identify user-uploads. Should be compared with those in the session-cookie,
     * to secure the cache from unauthorized access.
     * @return uploadId
     */
    public String createUploadId(){
        String uuid = UUID.randomUUID().toString();
        uploadIds.put(uuid, System.currentTimeMillis());
        return uuid;
    }

    /**
     * Checks if the given uploadId is valid (exists and not older than the MAXIMAL_CACHE_TIME)
     * @param uploadId
     * @return
     */
    public boolean isUploadIdValid(String uploadId){
        long timestamp = uploadIds.getOrDefault(uploadId, -1l);
        if(timestamp < 0) return false;
        if(System.currentTimeMillis() - timestamp > MAXIMAL_CACHE_TIME){
            uploadIds.remove(uploadId);
            Logger.error("dfhsdfbbfdfbsdfbdfjvbsrhgbbvd");
            return false;
        } else {
            return true;
        }
    }

    public ObjectNode addFilesToCache(final String uploadId, List<FilePart> fileParts) throws IOException {
        //Preparing Json Answer
        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        if(!isUploadIdValid(uploadId)) return result;
        CopyOnWriteArrayList<File> cachedFiles = null;
        synchronized (uploadId) {
            cachedFiles = fileList.get(uploadId);
            if (cachedFiles == null){
                cachedFiles = new CopyOnWriteArrayList<File>();
                fileList.put(uploadId, cachedFiles);
            }
        }

        for(FilePart part : fileParts){
            File tf = part.getFile();
            Files.copy(tf.toPath(), new File(TEMP_DIR.getAbsolutePath()+"/"+tf.getName()).toPath());
            File f = new File(TEMP_DIR.getAbsolutePath()+"/"+tf.getName());
            arrayNode.addObject()
                    .put("name", part.getFilename())
                    .put("size", f.length())
                    .put("url", calcDownloadPath(uploadId, f))
                    .put("thumbnailUrl", calcDownloadThumbnailPath(uploadId, f))
                    .put("deleteUrl", calcDeletePath(uploadId, f))
                    .put("deleteType", "DELETE");
            cachedFiles.add(f);
        }
        return result;
    }

    public ObjectNode deleteFileFromCache(final String uploadId, String filename){
        //Preparing Json Answer
        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        if(!isUploadIdValid(uploadId)) return result;

        CopyOnWriteArrayList<File> cachedFiles = fileList.get(uploadId);

        for(File f : cachedFiles){
            if(f.getName().equals(filename)){
                cachedFiles.remove(f);
                f.delete();
                arrayNode.addObject().put(filename, true);
            }
        }
        return  result;
    }

    public ByteArrayOutputStream getThumbnail(final String uploadId, String filename) throws IOException {
        Optional<File> of = loadFile(uploadId, filename);

        if(of.isPresent()){
            File f = of.get();
            BufferedImage image = ImageIO.read(f);
            BufferedImage thumbnail =
                    Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, THUMBNAIL_WIDTH, THUMBNAIL_HEIGTH, Scalr.OP_ANTIALIAS);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpg", baos);
            return baos;
        }
        return null; //handled in controller
    }

    public Optional<File> loadFile(final String uploadId, String filename){
        CopyOnWriteArrayList<File> cachedFiles = fileList.get(uploadId);
        File rcFile = null;
        for(File f : cachedFiles){
            if(f.getName().equals(filename)) rcFile = f;
        }
        return Optional.ofNullable(rcFile);
    }

    private String calcDownloadPath(final String uploadId, File f){
        return routes.UploadController.getFile(uploadId, f.getName()).path();
    }

    private String calcDownloadThumbnailPath(final String uploadId, File f){
        return routes.UploadController.getThumbnail(uploadId, f.getName()).path();
    }

    private String calcDeletePath(final String uploadId, File f){
        return routes.UploadController.delete(uploadId, f.getName()).path();
    }
}

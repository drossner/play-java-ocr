package modules.upload;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import play.Play;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.concurrent.duration.Duration;
import util.ImageHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Daniel on 28.11.2015.
 */
@Singleton
public class UploadHandler {

    private final long MAXIMAL_CACHE_TIME = 1000*60*60; //1 Stunde
    private final String TEMP_DIR_NAME = "/uploadTemp/";
    private final File TEMP_DIR;

    private ConcurrentSkipListMap<String, Long> uploadIds;
    private ConcurrentSkipListMap<String, CopyOnWriteArrayList<FileContainer>> fileList;

    private ImageHelper imageHelper;

    @Inject
    public UploadHandler(ImageHelper imageHelper) throws IOException {
        this.uploadIds = new ConcurrentSkipListMap<String, Long>();
        this.fileList = new ConcurrentSkipListMap<>();
        this.imageHelper = imageHelper;

        //cleanup code
        Akka.system().scheduler().schedule(
                Duration.create(30, TimeUnit.MINUTES),   // initial delay
                Duration.create(30, TimeUnit.MINUTES),   // run job every 30 minutes
                () -> {
                    cleanup();
                }, Akka.system().dispatcher());

        //Create custom temp dir (fuck that bug!!!!11111elf)
        TEMP_DIR = new File(Play.application().path().getAbsolutePath() + TEMP_DIR_NAME);
        TEMP_DIR.mkdir();
        File[] oldFiles = TEMP_DIR.listFiles();
        for(File f : oldFiles){
            f.delete();
        }
    }

    /**
     * Invalidates the given uploadId by setting the timestamp to -1
     * @param uploadId
     */
    public void invalidateUploadId(String uploadId){
        uploadIds.put(uploadId, -1L);
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
        if(!isTimestampValid(timestamp)){
            return false;
        } else {
            return true;
        }
    }

    private boolean isTimestampValid(long timestamp){
        return System.currentTimeMillis() - timestamp < MAXIMAL_CACHE_TIME;
    }

    /**
     * Add the uploaded files to the cache, referenced with the given upload id (uuid)
     * @param uploadId uuid
     * @param fileParts http fileparts
     * @return json response for the jquery upload plugin (client)
     * @throws IOException
     */
    public ObjectNode addFilesToCache(final String uploadId, List<FilePart> fileParts) throws IOException {
        //Preparing Json Answer
        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        if(!isUploadIdValid(uploadId)) return result;
        CopyOnWriteArrayList<FileContainer> cachedFiles = null;
        synchronized (uploadId) {
            cachedFiles = fileList.get(uploadId);
            if (cachedFiles == null){
                cachedFiles = new CopyOnWriteArrayList<FileContainer>();
                fileList.put(uploadId, cachedFiles);
            }
        }

        for(FilePart part : fileParts){
            File tf = part.getFile();
            Files.copy(tf.toPath(), new File(TEMP_DIR.getAbsolutePath()+"/"+tf.getName()).toPath());
            File f = new File(TEMP_DIR.getAbsolutePath()+"/"+tf.getName());
            FileContainer fc = new FileContainer.Builder()
                    .setContentType(part.getContentType())
                    .setFile(f)
                    .setFileName(part.getFilename())
                    .build();

            //check if file is a readable img
            if(imageHelper.fileIsValid(fc)){
                arrayNode.addObject()
                        .put("name", part.getFilename())
                        .put("size", f.length())
                        .put("url", calcDownloadPath(uploadId, f))
                        .put("thumbnailUrl", calcDownloadThumbnailPath(uploadId, f))
                        .put("deleteUrl", calcDeletePath(uploadId, f))
                        .put("deleteType", "DELETE");
                cachedFiles.add(fc);
            } else {
                arrayNode.addObject()
                        .put("name", part.getFilename())
                        .put("size", f.length())
                        .put("error", "File-type is not valid");
                f.delete();
            }
        }
        return result;
    }

    /**
     * Deletes uploaded files from cache.
     * @param uploadId upload uuid
     * @param filename file to delete
     * @return json response for the jquery upload plugin (client)
     */
    public ObjectNode deleteFileFromCache(final String uploadId, String filename){
        //Preparing Json Answer
        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        if(!isUploadIdValid(uploadId)) return result;

        CopyOnWriteArrayList<FileContainer> cachedFiles = fileList.get(uploadId);

        for(FileContainer fc : cachedFiles){
            File f = fc.getFile();
            if(f.getName().equals(filename)){
                cachedFiles.remove(f);
                f.delete();
                arrayNode.addObject().put(filename, true);
                cachedFiles.remove(fc);
            }
        }
        return result;
    }

    /**
     * Generates a Thumbnail of a given image file using a {@see ImageHelper}
     * @param uploadId upload uuid
     * @param filename file
     * @return the thumbnail
     * @throws IOException
     */
    public ByteArrayOutputStream getThumbnail(final String uploadId, String filename) throws IOException {
        Optional<FileContainer> of = loadFile(uploadId, filename);
        if(of.isPresent()){
            BufferedImage image = imageHelper.convertFile(of.get());
            image = imageHelper.getThumbnail(image);
            return imageHelper.convertBaos(image);
        }
        return null; //handled in controller
    }

    /**
     * Loads a specific {@see FileContainer} from the cache.
     * @param uploadId upload uuid
     * @param filename filename
     * @return Optional FileContainer
     */
    public Optional<FileContainer> loadFile(final String uploadId, String filename){
        CopyOnWriteArrayList<FileContainer> cachedFiles = fileList.get(uploadId);
        FileContainer rcFile = null;
        for(FileContainer fc : cachedFiles){
            File f = fc.getFile();
            if(f.getName().equals(filename)) rcFile = fc;
        }
        return Optional.ofNullable(rcFile);
    }

    /**
     * Load all FileContainer associated with the given upload id
     * @param uploadId upload uuid
     * @return all files
     */
    public List<FileContainer> loadFiles(final String uploadId){
        return fileList.get(uploadId);
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

    /**
     * Deletes all invalid Files from the HDD and the cache.
     */
    private void cleanup(){
        Iterator<Map.Entry<String, Long>> it = uploadIds.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Long> entry = it.next();
            if(!isTimestampValid(entry.getValue())){
                deleteUploadedFiles(entry.getKey());
                it.remove();
            }
        }
    }

    private void deleteUploadedFiles(String uploadId){
        CopyOnWriteArrayList<FileContainer> files = fileList.get(uploadId);
        if(files == null) return;
        for(FileContainer fc : files){
            fc.getFile().delete();
        }
        fileList.remove(uploadId);
    }
}

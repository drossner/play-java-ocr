package modules.upload;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import org.imgscalr.Scalr;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.concurrent.duration.Duration;

import javax.imageio.ImageIO;
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
    private final int THUMBNAIL_WIDTH = 80;
    private final int THUMBNAIL_HEIGTH = 80;
    private final String TEMP_DIR_NAME = "/uploadTemp/";
    private final File TEMP_DIR;

    private ConcurrentSkipListMap<String, Long> uploadIds;
    private ConcurrentSkipListMap<String, CopyOnWriteArrayList<FileContainer>> fileList;

    private ImageHelper imageHelper;

    public UploadHandler() throws IOException {
        this.uploadIds = new ConcurrentSkipListMap<String, Long>();
        this.fileList = new ConcurrentSkipListMap<>();
        this.imageHelper = new ImageHelper();

        //cleanup code
        Akka.system().scheduler().schedule(
                Duration.create(10, TimeUnit.MINUTES),   // initial delay
                Duration.create(10, TimeUnit.MINUTES),   // run job every 5 minutes
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
            //removed cause of efficency, better do async or in the scheduled job
            /*deleteUploadedFiles(uploadId);
            uploadIds.remove(uploadId);*/
            return false;
        } else {
            return true;
        }
    }

    private boolean isTimestampValid(long timestamp){
        return System.currentTimeMillis() - timestamp < MAXIMAL_CACHE_TIME;
    }

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

            arrayNode.addObject()
                    .put("name", part.getFilename())
                    .put("size", f.length())
                    .put("url", calcDownloadPath(uploadId, f))
                    .put("thumbnailUrl", calcDownloadThumbnailPath(uploadId, f))
                    .put("deleteUrl", calcDeletePath(uploadId, f))
                    .put("deleteType", "DELETE");
            cachedFiles.add(fc);
        }
        return result;
    }

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
        return  result;
    }

    public ByteArrayOutputStream getThumbnail(final String uploadId, String filename) throws IOException {
        Optional<FileContainer> of = loadFile(uploadId, filename);

        if(of.isPresent()){
            BufferedImage image = imageHelper.convertFile(of.get());
            BufferedImage thumbnail =
                    Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, THUMBNAIL_WIDTH, THUMBNAIL_HEIGTH, Scalr.OP_ANTIALIAS);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpeg", baos);
            return baos;
        }
        return null; //handled in controller
    }

    public Optional<FileContainer> loadFile(final String uploadId, String filename){
        CopyOnWriteArrayList<FileContainer> cachedFiles = fileList.get(uploadId);
        FileContainer rcFile = null;
        for(FileContainer fc : cachedFiles){
            File f = fc.getFile();
            if(f.getName().equals(filename)) rcFile = fc;
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
        fileList.remove(files);
    }
}

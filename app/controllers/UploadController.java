package controllers;

import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.OcrDeadboltHandler;
import modules.upload.FileContainer;
import modules.upload.UploadHandler;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.ImageHelper;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel on 27.11.2015.
 */
@Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
public class UploadController extends Controller {

    private UploadHandler uploadHandler;

    @Inject
    public UploadController(UploadHandler uploadHandler){
        this.uploadHandler = uploadHandler;
    }

    /**
     * überprüft, ob die dateien in dem datei container die richtigen dateiendungen haben
     * @param uploadId id des datei containers
     * @return ok result
     */
    public Result checkFiles(String uploadId) {
        List<FileContainer> filelist = uploadHandler.loadFiles(uploadId);
        ObjectNode result = Json.newObject();
        if(filelist != null && filelist.size() > 0){
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        return ok(result);
    }

    /**
     * lädt die dateien aus dem datei container mit der upload id auf den server hoch
     * @param uploadId id des datei containers
     * @return ok result
     */
    public F.Promise<Result> upload(String uploadId){
        Logger.debug("File upload started from " + session().get("session"));
        return F.Promise.promise(() -> {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            List<Http.MultipartFormData.FilePart> pictures = body.getFiles();
            //Json Answer
            ObjectNode result = null;
            try {
                result = uploadHandler.addFilesToCache(uploadId, pictures);

            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                return internalServerError(e.getMessage());
            }

            Logger.debug(result.toString());
            return ok(result);
        });

    }

    /**
     * löscht die datei mit dem übergebenen datei namen aus dem datei container, der die übergebene id hat
     * @param uploadId datei container id
     * @param file datei namen, der datei die gelöscht werden soll
     * @return ok result
     */
    public F.Promise<Result> delete(String uploadId, String file){
        return F.Promise.promise(() -> {
            ObjectNode result = uploadHandler.deleteFileFromCache(uploadId, file);
            Logger.debug(result.toString());
            return ok(result);
        });
    }

    /**
     * gibt die datei mit dem datei namen aus dem datei container mit der übergebenen id zurück
     * @param uploadId datei container id
     * @param file datei namen
     * @return file pfad
     */
    public F.Promise<Result> getFile(String uploadId, String file){
        return F.Promise.promise(() -> {
            Optional<FileContainer> of = uploadHandler.loadFile(uploadId, file);
            if(of.isPresent()){
                FileContainer fc = of.get();
                return ok().sendPath(fc.getFile().toPath(), false, fc.getFileName());
            } else {
                return (badRequest("No such file"));
            }
        });
    }

    /**
     * gibt das vorschaubild der übergebenen datei aus dem datei container mit der übergebenen id zurück
     * @param uploadId datei container id
     * @param file datei name
     * @return bild array stream
     */
    public F.Promise<Result> getThumbnail(String uploadId, String file){
        return F.Promise.promise(() -> {
            ByteArrayOutputStream baos = null;
            try {
                baos = uploadHandler.getThumbnail(uploadId, file);
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                return internalServerError(e.getMessage());
            }
            return ok(baos.toByteArray()).as(ImageHelper.OUTPUT_MIMETYPE);
        });
    }
}

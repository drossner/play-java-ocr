package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import modules.upload.UploadHandler;
import play.Logger;
import play.cache.CacheApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel on 27.11.2015.
 */
public class UploadController extends Controller {

    private UploadHandler uploadHandler;

    @Inject
    public UploadController(CacheApi cache, UploadHandler uploadHandler){
        this.uploadHandler = uploadHandler;
    }

    public Result upload(String uploadId){
        Logger.debug("File upload started from " + session().get("session"));
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
    }

    public Result delete(String uploadId, String file){
        ObjectNode result = uploadHandler.deleteFileFromCache(uploadId, file);
        Logger.debug(result.toString());
        return ok(result);
    }

    public Result getFile(String uploadId, String file){
        Optional<File> of = uploadHandler.loadFile(uploadId, file);
        if(of.isPresent()){
            return ok(of.get());
        } else {
            return (badRequest("No such file"));
        }
    }

    public Result getThumbnail(String uploadId, String file){
        ByteArrayOutputStream baos = null;
        try {
            baos = uploadHandler.getThumbnail(uploadId, file);
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError(e.getMessage());
        }
        return ok(baos.toByteArray()).as("image/jpg");
    }


}

package controllers;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.imgscalr.Scalr;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel on 27.11.2015.
 */
public class UploadController extends Controller {

    private CacheApi cache;

    @Inject
    public UploadController(CacheApi cache){
        this.cache = cache;
    }

    public Result upload(){
        Logger.debug("File upload started from " + session().get("session"));
        Http.MultipartFormData body = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> pictures = body.getFiles();

        final String cacheIdentifier = session().get("session") + ".files";

        //Json Answer
        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        LinkedList<File> fileList = cache.getOrElse(
               cacheIdentifier , () -> new LinkedList<File>()
        );

        for(Http.MultipartFormData.FilePart filePart : pictures){
            File file = filePart.getFile();
            fileList.add(file);
            arrayNode.addObject()
                    .put("name", filePart.getFilename())
                    .put("size", file.length())
                    .put("url", routes.UploadController.getFile(file.getName()).path())
                    .put("thumbnailUrl", routes.UploadController.getFile(file.getName()).path())
                    .put("deleteUrl", routes.UploadController.delete(file.getName()).path())
                    .put("deleteType", "DELETE");
        }

        cache.set(cacheIdentifier, fileList);
        Logger.debug(result.toString());
        return ok(result);
    }

    public Result delete(String file){
        final String cacheIdentifier = session().get("session") + ".files";
        LinkedList<File> fileList = cache.get(cacheIdentifier);

        ObjectNode result = Json.newObject();
        ArrayNode arrayNode = result.putArray("files");
        if(fileList != null){
            for(File f : fileList){
                if(f.getName().equals(file)){
                    fileList.remove(f);
                    arrayNode.addObject().put(file, true);
                }
            }
        }
        Logger.debug(result.toString());
        return ok(result);
    }

    public Result getFile(String file){
        final String cacheIdentifier = session().get("session") + ".files";
        LinkedList<File> fileList = cache.get(cacheIdentifier);
        if(fileList != null){
            for(File f : fileList){
                if(f.getName().equals(file)) {
                    try {
                        BufferedImage image = ImageIO.read(f);
                        BufferedImage thumbnail =
                                Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, 80, 80, Scalr.OP_ANTIALIAS);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(thumbnail, "jpg", baos);
                        return ok(baos.toByteArray()).as("image/jpg");
                    } catch (IOException e) {
                        Logger.error(e.getMessage(), e);
                        return internalServerError(e.getMessage());
                    }
                }
            }
        }
        return badRequest("no such file");
    }

}

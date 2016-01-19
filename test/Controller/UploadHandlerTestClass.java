package Controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import modules.upload.UploadHandler;
import play.libs.Json;
import play.mvc.Http;
import util.ImageHelper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 19.01.16.
 */
public class UploadHandlerTestClass extends UploadHandler {

    @Inject
    public UploadHandlerTestClass(ImageHelper imageHelper) throws IOException {
        super(imageHelper);
    }

    @Override
    public ObjectNode addFilesToCache(final String uploadId, List<Http.MultipartFormData.FilePart> fileParts) throws IOException{
        return Json.newObject().put("success", true);
    }
}

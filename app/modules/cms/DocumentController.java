package modules.cms;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class DocumentController {

    private CMSController cmsController;

    public DocumentController(CMSController cmsController){
        this.cmsController = cmsController;
    }

    public Document getDocumentByPath(Folder parentFolder, String fileName){
        return (Document) cmsController.getSession().getObjectByPath(parentFolder.getPath()+ "/"+ fileName);
    }

    public Document getDocumentById(String documentId){
        return (Document) cmsController.getSession().getObject(documentId);
    }

    public Document createDocument(Folder parentFolder, File file, String fileType) throws FileNotFoundException {
        String fileName = file.getName();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, fileName);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

        ContentStream contentStream = cmsController.getSession().getObjectFactory().createContentStream(
                fileName,
                file.length(),
                fileType,
                new FileInputStream(file)
        );

        Document document = null;
        try {
            document = parentFolder.createDocument(properties, contentStream, null);
            Logger.info("Created new document: " + document.getId() + "   " + document.getPaths());
        } catch (CmisContentAlreadyExistsException ccaee) {
            document = (Document) cmsController.getSession().getObjectByPath(parentFolder.getPath() + "/" + fileName);
            Logger.info("Document already exists: " + fileName);
        }
        document.getPaths();
        return document;
    }

    public boolean deleteDocument(String object){
        try {
            Document document = (Document) cmsController.getSession().getObject(object);
            document.delete();
            Logger.info("Deleted document");
            return true;
        } catch (CmisObjectNotFoundException ccaee) {
            Logger.info("Dokument not found");
            return false;
        }
    }

    public boolean downloadDocument(String object, String destinationPath){
        try {
            FileUtils.download((Document) cmsController.getSession().getObject(object), destinationPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BufferedImage readingImage(String object){
        Document document = (Document) cmsController.getSession().getObject(object);
        String filename = document.getName();
        InputStream stream = document.getContentStream().getStream();

        BufferedImage bufferedImage = null;
        try {
             bufferedImage = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }


}

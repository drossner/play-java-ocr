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

    /**
     * Konstruktor
     * initialisiert einen DocumentController
     * @param cmsController
     */
    public DocumentController(CMSController cmsController){
        this.cmsController = cmsController;
    }

    /**
     * gibt ein bestimmtes Dokument aus dem CMIS Repository zurück
     * @param parentFolder der Ordner in dem sich das Dokument befindet
     * @param fileName Name des Dokuments
     * @return document
     */
    public Document getDocumentByPath(Folder parentFolder, String fileName){
        return (Document) cmsController.getSession().getObjectByPath(parentFolder.getPath()+ "/"+ fileName);
    }

    /**
     * gibt ein bestimmtes Dokument aus dem CMIS Repository zurück
     * @param documentId  ID des zurückzugebenden Dokuments
     * @return document
     */
    public Document getDocumentById(String documentId){
        return (Document) cmsController.getSession().getObject(documentId);
    }

    /**
     * Erstellung eines Dokuments in dem CMIS Repository auf Basis eines bereits existierenden Dokuments.
     * Das Dokument existiert entweder nur Temporär oder in einem Verzeichnis des Betriebsystems
     * @param parentFolder Zielordner in dem das Dokument erstellt werden soll
     * @param file das existierde Dokument
     * @param fileType Art des Dokuments
     * @return document, das erstellte Dokument aus dem CMIS Repository
     * @throws FileNotFoundException
     */
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

    /**
     * Löscht ein Dokument aus dem CMIS Repositoy
     * @param object  id des zulöschenden Dokuments
     * @return ture wenn der Löschvorgang erfolgreich war
     */
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

    /**
     * Läd ein Dokument aus dem CMIS Repository
     * @param object id des Dokuments
     * @param destinationPath Pfad in dem das geladenen Dokument gespeichert werden soll
     * @return true wenn der Download erfolgreich war
     */
    public boolean downloadDocument(String object, String destinationPath){
        try {
            FileUtils.download((Document) cmsController.getSession().getObject(object), destinationPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Liest ein (Dokument)Bild aus dem CMIS Repository und erstellt ein BufferdImage
     * @param object id des (Dokument) Bildes
     * @return  Bufferedimage
     */
    public BufferedImage readingImage(String object){
        Document document = (Document) cmsController.getSession().getObject(object);
        InputStream stream = document.getContentStream().getStream();

        BufferedImage bufferedImage = null;
        try {
             bufferedImage = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }

    /**
     * Läd eine JSON Datei aus dem CMIS Repository
     * @param object id der JSON Datei
     * @return eine JSON Datei vom CMIS Repostory als Stream
     */
    public InputStream readingJSON(String object){
        Document document = (Document) cmsController.getSession().getObject(object);
        InputStream stream = document.getContentStream().getStream();
        return stream;
    }


}

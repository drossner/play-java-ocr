package modules.cms;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.impl.Base64;
import org.hibernate.mapping.List;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class CMSController {

    private CMSSession sessionCMS;

    private FolderController folderController;
    private DocumentController documentController;

    public CMSController(CMSSession session) {
        this.folderController = new FolderController(this);
        this.documentController = new DocumentController(this);

        this.sessionCMS = session;
    }


    public Session getSession(){
        return sessionCMS.getSession();
    }

    public String getUsername(){
        return sessionCMS.getUsername();
    }

    // Folder
    public Folder getWorkspaceFolder(){
        return folderController.getUserWorkspaceFolder();
    }

    public Folder getFolderbyPath(String folderPath){
        return folderController.getFolderByPath(folderPath);
    }

    public Folder getFolderById (String objectId){
        return folderController.getFolderByObjectId(objectId);
    }

    public ArrayList<Folder> getFolderTree(Folder parent){
        return  folderController.getFolderTree(parent);
    }

    public void getChildren(Folder folder){
        folderController.getChildren(folder);
    }

    public Folder createFolder(Folder parentFolder, String folderName){
        return folderController.createFolder(parentFolder, folderName);
    }

    public Folder updateFolder(Folder target, String newName){
        return folderController.updateFolder(target, newName);
    }

    public boolean deleteFolder(Folder target){
        return folderController.deleteFolder(target);
    }

    public ArrayList<Folder> listSharedFolder(){
        return folderController.listSharedFolder();
    }

    // Document
    public Document createDocument(String folderId, File file, String fileType) throws FileNotFoundException {
        folderId = folderId.trim();
        if(folderId == null || folderId.trim().equals("")){
            return documentController.createDocument(folderController.getUserWorkspaceFolder(), file, fileType);
        }
        return documentController.createDocument(folderController.getFolderByObjectId(folderId), file, fileType);
    }

    public Document createDocument(Folder target, File file, String fileType) throws FileNotFoundException {
        return documentController.createDocument(target, file, fileType);
    }

    public Document getDocument(String objectId) {
        return documentController.getDocumentById(objectId);
    }

    public boolean deleteDocument(String objectId){
        return documentController.deleteDocument(objectId);
    }

    public boolean downloadDocument(String objectId, String destinationPath){
        return documentController.downloadDocument(objectId, destinationPath);
    }

    public BufferedImage readingAImage (String objectId){
        return documentController.readingImage(objectId);
    }
    public InputStream readingJSON (String objectId){
        return documentController.readingJSON(objectId);
    }
}

package modules.cms;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.api.Session;
import play.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class CmsController {

    private SessionCMS sessionCMS;
    private static final String CMIS_ENDPOINT = "http://v22015042759824376.yourvserver.net:8080/nuxeo/atom/cmis";


    private FolderController folderController;
    private DocumentController documentController;

    public CmsController() {

        this.folderController = new FolderController(this);
        this.documentController = new DocumentController(this);
    }


    public SessionCMS createSession(String username, String password) {
        SessionHolder sessionHolder = SessionHolder.getInstance();
        sessionCMS = sessionHolder.retrieveSession(username);
        if (sessionCMS == null){
            sessionCMS = new SessionCMS(username, password);
            Logger.info("new Session");
            sessionHolder.storeSession(username, sessionCMS);
        }
        return sessionCMS;
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

    public Document createDocument(Folder target, File file, String fileType) throws FileNotFoundException {
        return documentController.createDocument(target, file, fileType);
    }

    public boolean deleteDocument(String objectId){
        return documentController.deleteDocument(objectId);
    }
}

package controllers;

import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrDeadboltHandler;
import modules.cms.CMSController;
import modules.cms.data.Folder;
import modules.cms.SessionHolder;
import modules.database.entities.User;
import play.Logger;
import play.api.libs.concurrent.Execution;
import play.db.jpa.JPA;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;

import java.util.ArrayList;

/**
 * Created by Benedikt Linke on 01.12.15.
 */
@Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
public class FolderController extends Controller {

    private CMSController cmsController;

    public Result getUserFolders(){
        initSessionCMS();

        ArrayList<Folder> folders = new ArrayList<>();

        try {
            org.apache.chemistry.opencmis.client.api.Folder foldercms = cmsController.getWorkspaceFolder();

            Folder folderRC = new Folder();
            folderRC.setId(foldercms.getId());
            folderRC.setParentId(foldercms.getParentId());
            folderRC.setTitle(foldercms.getName());

            ArrayList<org.apache.chemistry.opencmis.client.api.Folder> folderTree = cmsController.getFolderTree(foldercms);

            for (org.apache.chemistry.opencmis.client.api.Folder folder : folderTree){

                Folder tempFolder = new Folder();

                if (folder.getParentId().equals(folderRC.getId())){
                    tempFolder.setParentId("");
                }else{
                    tempFolder.setParentId(folder.getParentId());
                }
                tempFolder.setId(folder.getId());
                tempFolder.setTitle(folder.getName());
                tempFolder.setDescription(folder.getDescription());
                folders.add(tempFolder);
            }
        }catch (Exception e){
            Logger.warn("Userworkspace dosn't exist");
        }

        return ok(Json.toJson(folders));
    }

    public Result getSharedFolders(){
        initSessionCMS();

        ArrayList<org.apache.chemistry.opencmis.client.api.Folder> sharedFolders = cmsController.listSharedFolder();
        ArrayList<Folder> sharedFoldersTemp = new ArrayList<>();

        for (org.apache.chemistry.opencmis.client.api.Folder folder : sharedFolders){

            modules.cms.data.Folder tempFolder = new Folder();
            tempFolder.setParentId("");
            tempFolder.setId(folder.getId());
            tempFolder.setTitle("Geteilt von: " + folder.getCreatedBy());
            tempFolder.setDescription(folder.getDescription());
            sharedFoldersTemp.add(tempFolder);

            ArrayList<org.apache.chemistry.opencmis.client.api.Folder> folderTree = cmsController.getFolderTree(folder);
            for (org.apache.chemistry.opencmis.client.api.Folder tree : folderTree){
                Folder tempTreeFolder = new Folder();
                tempTreeFolder.setParentId(tree.getParentId());
                tempTreeFolder.setId(tree.getId());
                tempTreeFolder.setTitle(tree.getName());
                tempTreeFolder.setDescription(tree.getDescription());
                sharedFoldersTemp.add(tempTreeFolder);
            }
        }
        return ok(Json.toJson(sharedFoldersTemp));
    }

    private void initSessionCMS(){
        String username = session().get("session");
        User user = null;
        try {
            user = JPA.withTransaction(() ->
                    new modules.database.JobController().selectEntity(User.class, "eMail", username));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        cmsController = SessionHolder.getInstance().getController(user.getCmsAccount(), user.getCmsPassword());
    }
}



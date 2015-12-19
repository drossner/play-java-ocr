package controllers;

import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrDeadboltHandler;
import modules.cms.CMSController;
import modules.cms.data.Folder;
import modules.cms.SessionHolder;
import modules.database.entities.Job;
import modules.database.entities.User;
import play.Logger;
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
@SubjectPresent
public class FolderController extends Controller {

    public Result getUserFolders(){
        ArrayList<Folder> folders = new ArrayList<>();

        String username = session().get("session");
        User user = null;
        try {
            user = JPA.withTransaction(() ->
                    new modules.database.JobController().selectEntity(User.class, "eMail", username));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        CMSController controller = SessionHolder.getInstance().getController(user.getCmsAccount(), user.getCmsPassword());

/*
        Folder folder = new Folder();
        folder.setId("1");
        folder.setTitle("Folder1");
        folder.setDescription("das ist ein Ordner");
        folder.setParentId("");

        Folder folder1 = new Folder();
        folder1.setId("2");
        folder1.setTitle("Folder2");
        folder1.setDescription("das ist der Ordner 2");
        folder1.setParentId("1");

        folders.add(folder);
        folders.add(folder1);
        */


        org.apache.chemistry.opencmis.client.api.Folder foldercms = controller.getWorkspaceFolder();

        Folder folderRC = new Folder();
        folderRC.setId(foldercms.getId());
        folderRC.setParentId(foldercms.getParentId());
        folderRC.setTitle(foldercms.getName());

        ArrayList<org.apache.chemistry.opencmis.client.api.Folder> folderTree = controller.getFolderTree(foldercms);

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
        //TODO save in Parent get the right ParentId, at the moment it is ""
        return ok(Json.toJson(folders));
    }
}



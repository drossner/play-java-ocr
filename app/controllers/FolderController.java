package controllers;

import modules.database.entities.Job;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;

/**
 * Created by Benedikt Linke on 01.12.15.
 */
public class FolderController extends Controller {

    public Result getUserFolders(){
        ArrayList<Folder> folders = new ArrayList<>();

        //TODO select from database

        Folder folder = new Folder();
        folder.setId("1");
        folder.setTitle("Folder1");
        folder.setParentId("");

        Folder folder1 = new Folder();
        folder1.setId("2");
        folder1.setTitle("Folder2");
        folder1.setParentId("1");

        folders.add(folder);
        folders.add(folder1);



        return ok(Json.toJson(folders));
    }
}



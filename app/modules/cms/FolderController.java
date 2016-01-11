package modules.cms;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import play.Logger;

import java.util.*;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class FolderController {

    private CMSController cmsController;

    /**
     * Konstruktor
     * initialisiert einen FolderController
     * @param cmsController
     */
    public FolderController(CMSController cmsController){
        this.cmsController = cmsController;
    }

    /**
     * gibt den Workspace-Ordner eines Benutzers zurück
     * @return Ordner
     */
    public Folder getUserWorkspaceFolder(){
        String workspacePath = "/default-domain/UserWorkspaces/"+ cmsController.getUsername();
        return  (Folder) cmsController.getSession().getObjectByPath(workspacePath);
    }

    /**
     * gibt einen Ordner auf Basis eines Pfades zurück
     * @param folderpath Pfad des Ordners
     * @return Ordner
     */
    public Folder getFolderByPath(String folderpath){
        return (Folder) cmsController.getSession().getObjectByPath("/default-domain/UserWorkspaces/"+ folderpath);
    }

    /**
     * gibt einen Ordner auf Basis der Ordner-IDs zurück
     * @param objectId ID des Ordners
     * @return Ordner
     */
    public Folder getFolderByObjectId(String objectId){
        return (Folder) cmsController.getSession().getObject(objectId);
    }


    /**
     * Gibt den kompletten Ordner-Baum zurück, der sich in einem übergeordneten Ordner befinden
     * @param parent übergeordente Ordner bei dem begonnen wird
     * @return eine Liste von Ordnern
     */
    public ArrayList<Folder> getFolderTree(Folder parent) {
        ArrayList<Folder> folders = new ArrayList<Folder>();
        if (!cmsController.getSession().getRepositoryInfo().getCapabilities().isGetFolderTreeSupported()) {
            Logger.warn("getFolderTree not supported in this repository");
        } else {
            for (Tree<FileableCmisObject> t : parent.getFolderTree(-1)) {
                printFolderTree(t, folders);
            }
        }

        return folders;
    }


    /**
     * Hilfsmethode für die Funktion "getFolderTree"
     * @param tree
     * @param folders
     */
    private void printFolderTree(Tree<FileableCmisObject> tree, ArrayList<Folder> folders) {
        Logger.info("Folder " + tree.getItem().getName());
        folders.add((Folder) tree.getItem());
        for (Tree<FileableCmisObject> t : tree.getChildren()) {
            printFolderTree(t, folders);
        }
    }

    /**
     * Erstellen eines Unterordners
     * @param parentFolder Name des übergeordneten Ordners
     * @param folderName Name des neuen Ordners
     * @return gibt den neuen Ordner zurück
     */
    public Folder createFolder(Folder parentFolder, String folderName) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, folderName);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        Folder newFolder = parentFolder.createFolder(properties);
        Logger.info(newFolder.getName() + " created");
        return newFolder;
    }

    /**
     * Löscht einen Ordner und alle sein Inhalte: Unterordner sowie Dokumente
     * @param target der Ordner, der gelöscht werden soll
     * @return true wenn es erfolgreich war
     */
    public boolean deleteFolder(Folder target) {
        try {
            target.deleteTree(true, UnfileObject.DELETE, true);
            Logger.info("Folder deleted");
            return true;
        } catch (CmisObjectNotFoundException e) {
            Logger.info("Folder not found.");
            return false;
        }
    }

    /**
     * Unbenennen eines Ordners
     * @param folder des zu bearbeitenden Ordners
     * @param newFolderName neuer Ordnername
     * @return gibt den Ordner zurück
     */
    public Folder updateFolder(Folder folder, String newFolderName){
        try {
            Map<String, Object> updateProperties = new HashMap<String, Object>();
            updateProperties.put(PropertyIds.NAME, newFolderName);
            folder.updateProperties(updateProperties);
            Logger.info("Folder " + folder.getName() + " updated");
            return folder;
        } catch (CmisObjectNotFoundException e){
            Logger.info("Folder not found.");
        }
        return null;
    }

    /**
     * Löschen des aller Inhalte eines Benutzers
     */
    protected void deleteAllContent() {
        Folder rootFolder = getUserWorkspaceFolder();
        try {
            ItemIterable<CmisObject> children = rootFolder.getChildren();
            for (CmisObject cmisObject : children) {
                if ("cmis:folder".equals(cmisObject.getPropertyValue(PropertyIds.OBJECT_TYPE_ID))) {
                    List<String> notDeltedIdList = ((Folder)cmisObject)
                            .deleteTree(true, UnfileObject.DELETE, true);
                    if (notDeltedIdList != null && notDeltedIdList.size() > 0) {
                        throw new RuntimeException("Can not empty repo");
                    }
                } else {
                    cmisObject.delete(true);
                }
            }
        } catch (CmisObjectNotFoundException e){
            Logger.info("Can not delete All Content");
        }
    }

    /**
     * Gibt alle Folder und Dateien eines Ordners zurück
     * @param target übergeordneter Ordner
     * @return Liste von Ordnern und Dateien
     */
    public ItemIterable<CmisObject> getChildren(Folder target) {
        ItemIterable<CmisObject> children = target.getChildren();
        int ret = 0;
        for(CmisObject child : children) {
            System.out.println(child.getName());
            ret++;
        }
        return children;
    }


    /**
     * Sucht alle geteilten Ordner eines Benutzers mit Hilfe einer CMIS Query.
     * Dabei werden der Admin, der Systembenutzer und der eigene Benutzer ausgeschlossen
     * @return eine Liste aller geteilten Ordner
     */
    public ArrayList<Folder> listSharedFolder(){
        ArrayList<String> user = new ArrayList<String>();
        user.add("'system'"  + " AND ");
        user.add("'Administrator'"  + " AND ");
        user.add("'"+cmsController.getUsername()+"'");

        String whereStatment ="";
        for (String result: user){
            whereStatment += "NOT dc:creator=" + result;
        }

        // Holt den Abfragename vom cmis:objectId und cmis:parentId
        String myType = "cmis:folder";
        ObjectType type = cmsController.getSession().getTypeDefinition(myType);
        PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
        String objectIdQueryName = objectIdPropDef.getQueryName();

        PropertyDefinition<?> objectParendIdPropDef = type.getPropertyDefinitions().get(PropertyIds.PARENT_ID);
        String objectParentIdQueryName = objectParendIdPropDef.getQueryName();

        // führt Abfrage aus
        String queryString = "SELECT " + objectIdQueryName +", "+objectParentIdQueryName +" FROM " + type.getQueryName() + " WHERE " + whereStatment;
        ItemIterable<QueryResult> results = cmsController.getSession().query(queryString, false);

        ArrayList<Folder> allFolders = new ArrayList<Folder>();
        ArrayList<String> parentIdList = new ArrayList<String>();


        for (QueryResult qResult : results) {
            String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
            String parentId = qResult.getPropertyValueByQueryName(objectParentIdQueryName);

            allFolders.add( (Folder) cmsController.getSession().getObject(objectId));
            parentIdList.add(parentId);
        }

        // löscht alle duplicate mit selben parentID aus der Liste parentIdList
        HashSet hs = new HashSet();
        hs.addAll(parentIdList);
        parentIdList.clear();
        parentIdList.addAll(hs);


        for (Folder folder : allFolders){
            parentIdList.remove(folder.getId());
        }

        ArrayList<Folder> sharedFolder = new ArrayList<Folder>();
        for (Folder folder : allFolders){
            String objectId = folder.getId();
            String parentId = folder.getParentId();

            for (String parent : parentIdList){
                if (parent.equals(parentId))
                    sharedFolder.add((Folder) cmsController.getSession().getObject(objectId));
            }
        }

        return sharedFolder;
    }
}

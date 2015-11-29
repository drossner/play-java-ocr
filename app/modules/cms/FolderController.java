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

    private CmsController cmsController;

    public FolderController(CmsController cmsController){
        this.cmsController = cmsController;
    }

    public Folder getUserWorkspaceFolder(){
        String workspacePath = "/default-domain/UserWorkspaces/"+ cmsController.getUsername();
        return  (Folder) cmsController.getSession().getObjectByPath(workspacePath);
    }

    public Folder getFolderByPath(String folderpath){
        return (Folder) cmsController.getSession().getObjectByPath("/default-domain/UserWorkspaces/"+ folderpath);
    }

    public Folder getFolderByObjectId(String objectId){
        return (Folder) cmsController.getSession().getObject(objectId);
    }

    public Folder createFolder(Folder parentFolder, String folderName) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, folderName);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        Folder newFolder = parentFolder.createFolder(properties);
        Logger.info(newFolder.getName() + " created");
        return newFolder;
    }

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

    public ItemIterable<CmisObject> getChildren(Folder target) {
        ItemIterable<CmisObject> children = target.getChildren();
        int ret = 0;
        for(CmisObject child : children) {
            System.out.println(child.getName());
            ret++;
        }
        return children;
    }


    public ArrayList<Folder> listSharedFolder(){
        ArrayList<String> user = new ArrayList<String>();
        user.add("'system'"  + " AND ");
        user.add("'Administrator'"  + " AND ");
        user.add("'"+cmsController.getUsername()+"'");

        String whereStatment ="";
        for (String result: user){
            whereStatment += "NOT dc:creator=" + result;
        }

        // get the query name of cmis:objectId and cmis:parentId
        String myType = "cmis:folder";
        ObjectType type = cmsController.getSession().getTypeDefinition(myType);
        PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
        String objectIdQueryName = objectIdPropDef.getQueryName();

        PropertyDefinition<?> objectParendIdPropDef = type.getPropertyDefinitions().get(PropertyIds.PARENT_ID);
        String objectParentIdQueryName = objectParendIdPropDef.getQueryName();

        // execute query
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

        // Delete duplicate parentID fpr parentIdList
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

        printSharedfolder(sharedFolder);

        return sharedFolder;
    }


    private void printSharedfolder(ArrayList<Folder> folders){

        for (Folder folder : folders){
            String objectId = folder.getId();
            String name = folder.getName();
            System.out.println("Name: " +name + " ObjectId: "+  objectId);
        }
    }
}

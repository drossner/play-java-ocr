import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.entities.User;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.db.jpa.JPA;
import play.test.WithApplication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class CmsTest extends WithApplication {

    CMSController cmsController;

    String user = "ocr";
    String password = "ocr";

    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String testImage = "inputTestImage.png";
    String testImageOutput = "outputTestImage.png";


    @Before
    public void setupTest(){
            cmsController = SessionHolder.getInstance().getController(user, password);
    }

    @Test
    public void folderTest(){

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        Folder newFolder = cmsController.createFolder(workspaceFolder, "testfolder");
        String testFolderId = newFolder.getId();

        assertEquals(testFolderId, cmsController.getFolderById(testFolderId).getId());
        Folder testFolder = cmsController.getFolderById(testFolderId);

        Folder updatedFolder = cmsController.updateFolder(testFolder, "newName");
        assertEquals(updatedFolder.getName(), "newName");

        assertTrue(cmsController.deleteFolder(testFolder));

    }

    @Test
    public void documentTest(){
        File file = new File(path + testImage);
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);
            assertEquals(testDocument.getName(),testImage);
            Logger.info(testDocument.getContentUrl());
            assertTrue(cmsController.deleteDocument(testDocument.getId()));

        } catch (FileNotFoundException e) {
            Logger.info("File not found",e);
        }
    }

    @Test
    public void downloadDocumentTest(){
        File file = new File(path + testImage);
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);
            Logger.info("Start to downloading");
            assertTrue(cmsController.downloadDocument(testDocument.getId(), path + testImageOutput));
            assertTrue(cmsController.deleteDocument(testDocument.getId()));

            //Delete testImage form Dictionary
            File fileOutput = new File(path+ testImageOutput);
            fileOutput.delete();

        } catch (FileNotFoundException e) {
            Logger.info("File not found",e);
        }
    }

    @Test
    public void readingAImageTest(){
        File file = new File(path + testImage);
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);

            BufferedImage bufferedImage = cmsController.readingAImage(testDocument.getId());
            File f = new File(path+ testImageOutput);
            assertTrue(ImageIO.write(bufferedImage, "png", f));
            assertTrue(cmsController.deleteDocument(testDocument.getId()));

            //Delete testImage form Dictionary
            File fileOutput = new File(path+ testImageOutput);
            fileOutput.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSharedFoldersTest(){
        // Voraussetzung: Der User muss existieren und mit anderen User Ordner geteilt hab en
        cmsController = SessionHolder.getInstance().getController("test", "test");

        ArrayList<Folder> sharedFolders = cmsController.listSharedFolder();
        ArrayList<modules.cms.data.Folder> sharedFoldersTemp = new ArrayList<>();

        for (org.apache.chemistry.opencmis.client.api.Folder folder : sharedFolders){

            modules.cms.data.Folder tempFolder = new modules.cms.data.Folder();
            tempFolder.setParentId("");
            tempFolder.setId(folder.getId());
            tempFolder.setTitle("Geteilt von: " + folder.getCreatedBy());
            tempFolder.setDescription(folder.getDescription());
            sharedFoldersTemp.add(tempFolder);

            ArrayList<org.apache.chemistry.opencmis.client.api.Folder> folderTree = cmsController.getFolderTree(folder);
            for (org.apache.chemistry.opencmis.client.api.Folder tree : folderTree){
                modules.cms.data.Folder tempTreeFolder = new modules.cms.data.Folder();
                tempTreeFolder.setParentId(tree.getParentId());
                tempTreeFolder.setId(tree.getId());
                tempTreeFolder.setTitle(tree.getName());
                tempTreeFolder.setDescription(tree.getDescription());
                sharedFoldersTemp.add(tempTreeFolder);
            }
        }

        // print Sharedfolders
        for (modules.cms.data.Folder folder : sharedFoldersTemp){
            String objectId = folder.getId();
            String name = folder.getTitle();
            Logger.info("Name: " +name + " ObjectId: "+  objectId);
        }

        assertTrue(!sharedFoldersTemp.isEmpty());
    }
}

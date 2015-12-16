import modules.cms.CMSController;
import modules.cms.SessionHolder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class CmsTest {

    CMSController cmsController;

    String user = "ocr";
    String password = "ocr";


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
        File file = new File("./test/testFiles/Wissenschaftlicher_Artikel.PNG");
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);
            assertEquals(testDocument.getName(),"Wissenschaftlicher_Artikel.PNG");
            Logger.info(testDocument.getContentUrl());
            assertTrue(cmsController.deleteDocument(testDocument.getId()));

        } catch (FileNotFoundException e) {
            Logger.info("File not found",e);
        }
    }

    @Test
    public void downloadDocumentTest(){
        String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";

        File file = new File(path + "Wissenschaftlicher_Artikel.PNG");
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();

        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);
            Logger.info("Start to downloading");
            assertTrue(cmsController.downloadDocumant(testDocument.getId(), path + "/downloaded/downloadTest.png"));
            assertTrue(cmsController.deleteDocument(testDocument.getId()));

        } catch (FileNotFoundException e) {
            Logger.info("File not found",e);
        }
    }

    @Test
    public void readingAImageTest(){
        String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";

        File file = new File(path + "Wissenschaftlicher_Artikel.PNG");
        String fileType = "File";

        Folder workspaceFolder = cmsController.getWorkspaceFolder();
        try {
            Document testDocument = cmsController.createDocument(workspaceFolder,file, fileType);

            BufferedImage bufferedImage = cmsController.readingAImage(testDocument.getId());
            File f = new File(path+"downloaded/testFile.png");
            assertTrue(ImageIO.write(bufferedImage, "png", f));
            assertTrue(cmsController.deleteDocument(testDocument.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

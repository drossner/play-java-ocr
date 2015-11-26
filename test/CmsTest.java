import modules.cms.CmsController;
import modules.cms.SessionCMS;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class CmsTest {

    public static final String BASE_URL = "http://localhost:8080/nuxeo/atom/cmis";

    SessionCMS sessionCMS;
    CmsController cmsController;

    String user = "testuser";
    String password = "test";



    @Before
    public void setupTest(){
        sessionCMS = new SessionCMS(user, password, BASE_URL);
        cmsController = new CmsController(sessionCMS);
    }

    @Test
    public void folderTest() {
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

}

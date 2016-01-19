import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import modules.upload.FileContainer;
import modules.upload.UploadHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.test.WithApplication;
import util.ImageHelper;
import play.mvc.Http.MultipartFormData.FilePart;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Created by daniel on 17.01.16.
 */
public class UploadHandlerTest extends WithApplication{

    @Inject
    private UploadHandler uploadHandler;

    @Before
    public void setup() throws IOException {
        //mock unneeded dependency
        ImageHelper imgHelper = Mockito.mock(ImageHelper.class);
        //accept the firs file as valid img, second as false
        when(imgHelper.fileIsValid(isA(File.class))).thenReturn(true).thenReturn(false);
        when(imgHelper.fileIsValid(isA(FileContainer.class))).thenReturn(true).thenReturn(false);
        uploadHandler = new UploadHandler(imgHelper);
    }

    /**
     * Generates some UploadIDs and deletes them
     */
    @Test
    public void testUploadIdHandling(){
        final int runs = 10;
        LinkedList<String> ids = new LinkedList<>();
        for(int i = 0; i < runs; i++){
            ids.add(uploadHandler.createUploadId());
        }
        Iterator<String> it1 = ids.iterator();
        while(it1.hasNext()){
            String temp = it1.next();
            assertTrue("Generated Upload-ID is valid", uploadHandler.isUploadIdValid(temp));
            //invalidate
            uploadHandler.invalidateUploadId(temp);
            assertFalse(uploadHandler.isUploadIdValid(temp));
        }
    }

    @Test
    public void fileHandling() throws IOException {
        //init uploadID
        final String uploadID = uploadHandler.createUploadId();
        //create some dummy fileParts
        List<FilePart> file1 = new LinkedList<>();
        //mock filepart
        FilePart part1 = Mockito.mock(FilePart.class);
        when(part1.getContentType()).thenReturn("image/jpeg");
        when(part1.getFile()).thenReturn(File.createTempFile("test", "dummy"));
        when(part1.getFilename()).thenReturn("test.jpg");
        file1.add(part1);

        List<FilePart> file2 = new LinkedList<>();
        //mock filepart
        FilePart part2 = Mockito.mock(FilePart.class);
        when(part2.getContentType()).thenReturn("image/jpeg");
        when(part2.getFile()).thenReturn(File.createTempFile("test", "dummy"));
        when(part2.getFilename()).thenReturn("test2.jpg");
        file2.add(part2);

        //"upload" files
        ObjectNode node1 = uploadHandler.addFilesToCache(uploadID, file1);
        ObjectNode node2 = uploadHandler.addFilesToCache(uploadID, file2);

        //validate and check with expected json
        //first one is valid
        assertTrue("Not empty Json answer", node1.elements().hasNext());
        assertEquals(1, node1.elements().next().size());
        assertTrue(node1.elements().next().isArray());
        ArrayNode arrayNode1 = (ArrayNode) node1.elements().next();
        assertNotNull(arrayNode1);
        assertEquals("test.jpg", arrayNode1.findValue("name").textValue());
        assertEquals(0, arrayNode1.findValue("size").intValue());
        assertTrue(arrayNode1.findValue("url").textValue().startsWith("/getfile/"+uploadID+"/"));
        assertTrue(arrayNode1.findValue("thumbnailUrl").textValue().startsWith("/getPrev/"+uploadID+"/"));
        assertTrue(arrayNode1.findValue("deleteUrl").textValue().startsWith("/delete/"+uploadID+"/"));

        //second is invalid
        assertTrue("Not empty Json answer", node2.elements().hasNext());
        assertEquals(1, node2.elements().next().size());
        assertTrue(node2.elements().next().isArray());
        ArrayNode arrayNode2 = (ArrayNode) node2.elements().next();
        assertNotNull(arrayNode2);
        assertEquals("test2.jpg", arrayNode2.findValue("name").textValue());
        assertEquals(0, arrayNode2.findValue("size").intValue());
        assertEquals("File-type is not valid", arrayNode2.findValue("error").textValue());

        //load the files
        //only the first upload should be stored
        List<FileContainer> files = uploadHandler.loadFiles(uploadID);
        assertEquals(1, files.size());
        assertEquals("test.jpg", files.get(0).getFileName());
        //save real file name for next test
        String fn = files.get(0).getFile().getName();

        //delete all files
        //check json answer
        ObjectNode node3 = uploadHandler.deleteFileFromCache(uploadID, fn);
        assertTrue("Not empty Json answer", node3.elements().hasNext());
        assertEquals(1, node3.elements().next().size());
        assertTrue(node3.elements().next().isArray());
        ArrayNode arrayNode3 = (ArrayNode) node3.elements().next();
        assertNotNull(arrayNode3);
        assertEquals(1, arrayNode3.size());
        assertEquals(true, arrayNode3.findValue(fn).booleanValue());
        //check cache
        assertEquals(0, uploadHandler.loadFiles(uploadID).size());
    }

}

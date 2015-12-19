import modules.export.Export;
import modules.export.Fragment;
import modules.export.impl.DocxExport;
import modules.export.impl.PdfExport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Benedikt Linke on 17.12.15.
 */
public class PDFTest {

    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String fileName = "outputPDF";

    @Before
    public void setupTest(){
        export = new PdfExport();
        export.initialize(path, fileName);
    }

    @Test
    public void createTextPDFTest(){

        Fragment temp = new DataCreator().getTextFragment();
        export.export(temp);
        export.newPage();
        export.export(temp);
        export.newPage();
        assertNotNull(export.finish());
    }

    @Test
    public void createImagePDFTest() throws IOException {

        Fragment temp = new DataCreator().getImageFragment();
        export.export(temp);


        assertNotNull(export.finish());
    }



    public class DataCreator{
        public Fragment getTextFragment(){
            Fragment fragment = new Fragment();

            fragment.setContent("HelloWorld \n tester \n tesgadvfda");
            fragment.setStartX(50);
            fragment.setStartY(70);
            fragment.setEndX(70);
            fragment.setEndY(30);
            return fragment;
        }

        public Fragment getImageFragment() throws IOException {
            Fragment fragment = new Fragment();

            fragment.setContent(ImageIO.read(new File("/Users/Ben/OCR/play-java-ocr/test/testFiles/Anfahrt.png")));
            fragment.setStartX(0);
            fragment.setStartY(30);
            fragment.setEndX(20);
            fragment.setEndY(50);
            return fragment;
        }

    }
}

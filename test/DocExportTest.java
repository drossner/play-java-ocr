import modules.export.Export;
import modules.export.Fragment;
import modules.export.impl.DocxExport;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Benedikt Linke on 12.12.15.
 */
public class DocExportTest {


    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String fileName = "outputDocx";

    @Before
    public void setupTest(){
    }

    @Test
    public void createDocxTextTest(){
        export = new DocxExport();
        export.initialize(path, fileName);
        Fragment temp = new DataCreator().getTextFragment();
        export.export(temp);
        assertNotNull(export.finish());
    }

    @Test
    public void createDocxPicTest() throws IOException {
        export = new DocxExport();
        export.initialize(path, fileName);
        Fragment temp = new DataCreator().getImageFragment();
        export.export(temp);
        assertNotNull(export.finish());
    }


    public class DataCreator{
        public Fragment getTextFragment(){
            Fragment fragment = new Fragment();

            fragment.setContent("HelloWorld");
            fragment.setStartX(50);
            fragment.setStartY(50);
            fragment.setEndX(70);
            fragment.setEndY(70);
            return fragment;
        }

        public Fragment getImageFragment() throws IOException {
            Fragment fragment = new Fragment();

            fragment.setContent(ImageIO.read(new File("/Users/Ben/OCR/play-java-ocr/test/testFiles/Wissenschaftlicher_Artikel.PNG")));
            fragment.setStartX(50);
            fragment.setStartY(50);
            fragment.setEndX(70);
            fragment.setEndY(70);
            return fragment;
        }

    }
}

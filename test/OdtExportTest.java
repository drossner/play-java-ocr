import modules.export.Export;
import modules.export.Fragment;
import modules.export.impl.DocxExport;
import modules.export.impl.OdtExport;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Benedikt Linke on 20.12.15.
 */
public class OdtExportTest {
    /*
    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String fileName = "outputDocx";

    ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Before
    public void setupTest(){
        export = new OdtExport();
        export.initialize(path, fileName, true);
    }

    @Test
    public void createOdtTextTest(){

        Fragment temp = new DataCreator().getTextFragment();


        export.export(temp);

        assertNotNull(export.finish());
    }

    @Test
    public void createOdtPicTest() throws IOException {

        Fragment temp = new DataCreator().getImageFragment();
        export.export(temp);
        assertNotNull(export.finish());
    }

    @Test
    public void createOdtTextAndPicTest() throws IOException {
        Fragment temp = new DataCreator().getTextFragment();
        export.export(temp);
        export.newPage();
        Fragment temp2 = new DataCreator().getImageFragment();
        export.export(temp2);
        assertNotNull(export.finish());
    }

    public class DataCreator{
        public Fragment getTextFragment(){
            Fragment fragment = new Fragment();

            fragment.setContent("Hello \nWorld");
            fragment.setStartX(50);
            fragment.setStartY(50);
            fragment.setEndX(70);
            fragment.setEndY(70);
            return fragment;
        }

        public Fragment getImageFragment() throws IOException {
            Fragment fragment = new Fragment();

            fragment.setContent(ImageIO.read(new File("/Users/Ben/OCR/play-java-ocr/test/testFiles/Wissenschaftlicher_Artikel.PNG")));
            fragment.setStartX(10);
            fragment.setStartY(30);
            fragment.setEndX(30);
            fragment.setEndY(60);
            return fragment;
        }

    }*/
}

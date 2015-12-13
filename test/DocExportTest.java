import modules.export.Export;
import modules.export.Fragment;
import modules.export.impl.DocxExport;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

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
    public void createDocxTest(){
        export = new DocxExport();
        export.initialize(path, fileName);
        Fragment temp = new DataCreator().getFragment();
        export.export(temp);
        assertNotNull(export.finish());
    }


    public class DataCreator{
        public Fragment getFragment(){
            Fragment fragment = new Fragment();

            fragment.setContent("HelloWorld");
            fragment.setStartX(50);
            fragment.setStartY(50);
            fragment.setEndX(70);
            fragment.setEndY(70);
            return fragment;
        }

    }
}

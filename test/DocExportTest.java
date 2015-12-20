import com.fasterxml.jackson.databind.ObjectMapper;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.export.Export;
import modules.export.Fragment;
import modules.export.impl.DocxExport;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import play.Logger;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Benedikt Linke on 12.12.15.
 */
public class DocExportTest {


    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String fileName = "outputDocx";

    ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Before
    public void setupTest(){
        export = new DocxExport();
        export.initialize(path, fileName, true);
        parseJson();
    }

    @Test
    public void createDocxTextTest(){

        Fragment temp = new DataCreator().getTextFragment();

        testCoordinates();
        // your code
        for(Fragment object: list){
            export.export(object);
        }
        assertNotNull(export.finish());
    }

    @Test
    public void createDocxPicTest() throws IOException {

        Fragment temp = new DataCreator().getImageFragment();
        export.export(temp);
        assertNotNull(export.finish());
    }

    @Test
    public void createNewPageTest() throws IOException {
        Fragment temp = new DataCreator().getTextFragment();
        export.export(temp);
        export.newPage();

        Fragment tempImg = new DataCreator().getImageFragment();
        export.export(tempImg);
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
            fragment.setStartX(10);
            fragment.setStartY(30);
            fragment.setEndX(300);
            fragment.setEndY(60);
            return fragment;
        }

    }

    private void parseJson(){
        String objectIdJSON = "4eecb77c-0152-49b7-840e-25cbb7c5322a";

        ObjectMapper mapper = new ObjectMapper();

        CMSController controller = SessionHolder.getInstance().getController("ocr", "ocr");
        InputStream inputStream = controller.readingJSON(objectIdJSON);

        try {
            Map<String, ArrayList> jsonMap = mapper.readValue(inputStream, Map.class);

            for (ArrayList object: jsonMap.values())
            {
                for(Object node: object){

                    String jsonNode = node.toString().split("=")[1].replaceAll("}", "");

                    //System.out.println("node: " + jsonNode);

                    Fragment temp = new Fragment();
                    temp.setContent(jsonNode);
                    temp.setStartX(10);
                    temp.setStartY(30);
                    temp.setEndX(50);
                    temp.setEndY(30);

                    list.add(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testCoordinates(){

        Fragment test1 = list.get(0);
        test1.setEndX(0.883424408014572);
        test1.setStartX(0.0382513661202186);
        test1.setEndY(0.25);
        test1.setStartY(0.177659574468085);

        Fragment test2 = list.get(1);
        test2.setEndX(0.948998178506375);
        test2.setStartX(0.0473588342440801);
        test2.setEndY(0.7574468085106389);
        test2.setStartY(0.311702127659574);

        Fragment test3 = list.get(2);
        test3.setEndX(0.918032786885246);
        test3.setStartX(0.0491803278688525);
        test3.setEndY(0.89468085106383);
        test3.setStartY(0.80531914893617);



    }
}

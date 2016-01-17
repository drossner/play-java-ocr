import com.fasterxml.jackson.databind.ObjectMapper;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Benedikt Linke on 17.12.15.
 */
public class PDFTest {
/*
    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String fileName = "outputPDF";

    ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Before
    public void setupTest(){
        export = new PdfExport();
        export.initialize(path, fileName, true);

        parseJson();
    }

    @Test
    public void createTextPDFTest(){

        Fragment temp = new DataCreator().getTextFragment();
        testCoordinates();
        // your code
        for(Fragment object: list){
            export.export(object);
        }
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



    }*/
}

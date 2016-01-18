package Exporter;

import control.result.ResultFragment;
import control.result.Type;
import modules.export.Export;
import modules.export.Fragment;
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

    Export export;
    String path = "/Users/Ben/OCR/play-java-ocr/test/testFiles/";
    String testImage = "inputTestImage.png";
    String testBlindText = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\n" +
            " Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque \n" +
            "penatibus et magnis dis parturient montes, nascetur ridiculus mus. \n" +
            "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. \n" +
            "Nulla consequat massa quis enim. Donec pede justo, fringilla vel, \n" +
            "aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, \n" +
            "imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis\n" +
            " pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi." +
            " Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, \n" +
            "consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra \n" +
            "quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque \n" +
            " rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper \n" +
            "ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget \n" +
            "condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ";
    String fileName = "outputODT";

    ArrayList<ResultFragment> list = new ArrayList<ResultFragment>();

    @Before
    public void setupTest() throws IOException {
        export = new OdtExport();
        export.initialize(path, path+fileName, true);

        list.add(new DataCreator().getTextFragment());
        list.add(new DataCreator().getImageFragment());
    }

    @Test
    public void createOdtTextTest() {
        ResultFragment temp = list.get(0);
        export.export(temp);
        assertNotNull(export.finish());
    }

    @Test
    public void createOdtPicTest() throws IOException {
        ResultFragment temp = list.get(1);
        export.export(temp);
        assertNotNull(export.finish());
    }

    @Test
    public void createOdtTextAndPicTest() throws IOException {
        ResultFragment tempTest = list.get(0);
        export.export(tempTest);

        export.newPage();

        ResultFragment tempImg = list.get(1);
        export.export(tempImg);

        assertNotNull(export.finish());
    }

    public class DataCreator{
        public ResultFragment getTextFragment(){
            ResultFragment fragment = new ResultFragment();

            fragment.setResult(testBlindText);
            fragment.setEndX(0.883424408014572);
            fragment.setStartX(0.0382513661202186);
            fragment.setEndY(0.25);
            fragment.setStartY(0.177659574468085);
            fragment.setType(Type.TEXT);
            return fragment;
        }

        public ResultFragment getImageFragment() throws IOException {
            ResultFragment fragment = new ResultFragment();

            fragment.setResult(ImageIO.read(new File(path + testImage)));
            fragment.setEndX(0.948998178506375);
            fragment.setStartX(0.0473588342440801);
            fragment.setEndY(0.7574468085106389);
            fragment.setStartY(0.311702127659574);
            fragment.setType(Type.IMAGE);
            return fragment;
        }
    }
}

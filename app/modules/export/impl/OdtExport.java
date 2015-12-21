package modules.export.impl;

import control.result.ResultFragment;
import modules.export.Export;
import modules.export.Fragment;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.style.MasterPage;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.text.Paragraph;
import play.Logger;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Benedikt Linke on 10.12.2015.
 */
public class OdtExport implements Export {

    String path;
    String fileName;

    TextDocument document;
    MasterPage master;


    @Override
    public void initialize(String path, String fileName, boolean landscape) {
        this.path = path;
        this.fileName = fileName;

        try {
            document = TextDocument.newTextMasterDocument();
            master = MasterPage.getOrCreateMasterPage(document, "Master");
            configureMasterPage(landscape);
        }  catch (Exception e) {
            Logger.info("ERROR: unable to create output file.");
        }

    }

    @Override
    public void export(ResultFragment fragment) {

        double pageHeight = master.getPageHeight()/10;
        double pageWidth = master.getPageWidth()/10;

        System.out.println("Höhe: "+ pageHeight + "cm");
        System.out.println("Breite: "+ pageWidth + "cm");


        double startX = pageWidth/100 * (fragment.getStartX() * 100);
        double startY = pageHeight/100 * (fragment.getStartY() * 100);
        double endX =  pageWidth/100 * (fragment.getEndX() * 100);
        double endY = pageHeight/100 * (fragment.getEndY() * 100);
        double width = endX - startX;
        double height = endY - startY;

        System.out.println("startX: "+ startX + "cm");
        System.out.println("endX: "+ endX + "cm");
        System.out.println("startY: "+ startY + "cm");
        System.out.println("heigth: "+ height + "cm");
        System.out.println("width: "+ width + "cm");


        if(fragment.getType() == control.result.Type.TEXT) {
            setText((String) fragment.getResult(),startX, startY, width, height);
        }else{
            setImage((BufferedImage)fragment.getResult(),startX, startY, width, height);
        }

    }

    @Override
    public void newPage() {
        document.addPageBreak();
    }

    @Override
    public File finish() {
        File file = null;
        try {
            document.save(path+fileName+".odt");
            file = new File(path+fileName+".odt");
        } catch (Exception e) {
            Logger.info("ERROR: unable to create output file.");
        }

        return file;
    }

    private void setText(String content, double startX, double startY, double width, double height){
        // add paragraph

        Paragraph paragraph = document.addParagraph("");

        Textbox box = paragraph.addTextbox(new FrameRectangle(startX,startY,width,height, StyleTypeDefinitions.SupportedLinearMeasure.CM));
        box.setTextContent(content);
    }


    private void setImage(BufferedImage content, double startX, double startY, double width, double height){

        // add image
        try {
            File file = new File(path + new Date().getTime() +"result.png");
            ImageIO.write(content, "png", file);

            Paragraph para = document.addParagraph("");
            Image image = Image.newImage(para, new URI(file.getPath()));
            FrameRectangle rectangle = new FrameRectangle(startX,startY,width,height, StyleTypeDefinitions.SupportedLinearMeasure.CM);
            image.setRectangle(rectangle);

            Logger.info("file delete: " + file.delete());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureMasterPage(boolean landscape){
        //create a customized page style with specified width, height, margins and other properties.
        try {
            master.setPageWidth(210);

            master.setPageHeight(297);

            if (landscape) {
                master.setPrintOrientation(StyleTypeDefinitions.PrintOrientation.LANDSCAPE);
            }

            master.setMargins(0, 0, 0, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

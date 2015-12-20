package modules.export.impl;

import modules.export.Export;
import modules.export.Fragment;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by Benedikt Linke on 10.12.2015.
 */
public class PdfExport implements Export {

    String path;
    String fileName;
    PDDocument doc = null;
    PDPage page = null;
    PDFont font = PDType1Font.TIMES_ITALIC;
    float fontSize = 12;

    PDRectangle rect = null;

    int pageCounter = 0;

    @Override
    public void initialize(String path, String fileName, boolean landscape) {
        this.path = path;
        this.fileName = fileName;

        doc = new PDDocument();
        page = new PDPage(PDPage.PAGE_SIZE_A4);

        rect = page.getMediaBox();
        doc.addPage(page);

        font = PDType1Font.HELVETICA;

    }

    @Override
    public void export(Fragment fragment) {
        float startX = (float) (rect.getWidth()/100 *fragment.getStartX()*100); //lo
        float startY = (float) (rect.getHeight()/100 *fragment.getStartY()*100); //lo

        float endX = (float) (rect.getWidth()/100 *fragment.getEndX()*100); //lo
        float endY = (float) (rect.getHeight()/100 *fragment.getEndY()*100); //lo

        float width = endX - startX;
        float height = endY - startY;

        /*
        System.out.println("Gesamt-Höhe: " +rect.getHeight());
        System.out.println("Gesamt-Breite: " + rect.getWidth());
        System.out.println("");
        System.out.println("width: " + width);
        System.out.println("height: " + height);
        System.out.println("startX: " + startX);
        System.out.println("startY: " + startY);
        System.out.println("");
        System.out.println("endX: " + endX);
        System.out.println("endY: " + endY);
        System.out.println("");
        System.out.println("");
        */

        if(fragment.getContent() instanceof String) {
            setText((String)fragment.getContent(), startX, rect.getHeight()-startY);
        } else {
            setImage((BufferedImage) fragment.getContent(), startX, rect.getHeight()-startY-width);
        }
    }

    @Override
    public void newPage(){
        doc.addPage(new PDPage(PDPage.PAGE_SIZE_A4));
        pageCounter++;
    }

    @Override
    public File finish() {
        File file = null;

        try {
            doc.save(path+fileName+".pdf");
            doc.close();

            file = new File(path+fileName+".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void setText(String content, float startX, float startY){

        PDPage test =  (PDPage) doc.getDocumentCatalog().getAllPages().get(pageCounter);
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(doc, test, true, true);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            contentStream.appendRawCommands(fontSize + " TL\n");
            contentStream.moveTextPositionByAmount(startX, startY);
            for(String line : getLines(content)) {
                contentStream.drawString(line);
                contentStream.appendRawCommands("T*\n");
            }
            contentStream.endText();
            contentStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setImage(BufferedImage content, float x, float y){
        PDXObjectImage image = null;
        try {
            image = new PDJpeg(doc, content);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.drawImage(image,x,y);
            contentStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void drawRect(float startX, float startY, float width, float height){
        PDPageContentStream cos = null;
        try {
            cos = new PDPageContentStream(doc, page);
            cos.setNonStrokingColor(Color.RED);
            cos.fillRect(startX, startY, width, height);
            cos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Break the text in lines
     * @return
     */
    private ArrayList<String> getLines(String content) {
        ArrayList<String> result = new ArrayList<String>();

        String[] lines = content.split("\n");

        for(int i=0;i<lines.length;i++) {
            lines[i] = lines[i].trim();
            result.add(lines[i]);
        }

        return result;
    }

    //TODO: doesn't work
    private void setOrientation(boolean landscape){
        if (landscape){
            page.setRotation(90);
        }
    }
}

package modules.export.impl;

import control.result.ResultFragment;
import control.result.Type;
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

    /**
     * Initialisiert ein Textdokument
     * @param path Speicherort
     * @param fileName Names des Dokuments
     * @param landscape Orientation des Dokuments
     */
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

    /**
     * Setzt den Content in dem Textdokument
     * @param fragment enthält ein Bild oder Text sowie Positionierungsangaben
     */
    @Override
    public void export(ResultFragment fragment) {

        // Definieren der Position des einzufügendes Elemtens
        float startX = (float) (rect.getWidth()/100 *fragment.getStartX()*100); //lo
        float startY = (float) (rect.getHeight()/100 *fragment.getStartY()*100); //lo

        float endX = (float) (rect.getWidth()/100 *fragment.getEndX()*100); //lo
        float endY = (float) (rect.getHeight()/100 *fragment.getEndY()*100); //lo

        float width = endX - startX;
        float height = endY - startY;

        // Überprüfung welcher Type von Content eingefügt werden soll
        if(fragment.getType() == Type.TEXT) {
            setText((String) fragment.getResult(), startX, rect.getHeight()-startY);
        } else {
            setImage((BufferedImage) fragment.getResult(), startX, rect.getHeight()-startY-width, width, height);
        }
    }

    /**
     * Seitenumbruch in einen Dokument erzeugen
     */
    @Override
    public void newPage(){
        doc.addPage(new PDPage(PDPage.PAGE_SIZE_A4));
        pageCounter++;
    }

    /**
     * Speichert das Dokument in einer Datei ab
     * @return das gespeicherte Dokument
     */
    @Override
    public File finish() {
        File file = null;

        try {
            doc.save(fileName+".pdf");
            doc.close();

            file = new File(fileName+".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Fügt ein Test im Textdokument ein
     * @param content Text
     * @param startX Startposition des Textes
     * @param startY Startposition des Textes
     */
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

    /**
     * Fügt ein Bild im Textdokument ein
     * @param content Bild (BufferdImage)
     * @param x Startposition des Textes
     * @param y Startposition des Textes
     * @param width Breite des Bildes
     * @param height Höhe des Bildes
     */
    private void setImage(BufferedImage content, float x, float y, float width, float height){
        PDXObjectImage image = null;
        try {
            image = new PDJpeg(doc, content);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
            contentStream.drawXObject(image,x,y, width, height);
            contentStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Break the text in lines
     * @return String-Array
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

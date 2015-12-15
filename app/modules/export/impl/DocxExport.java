package modules.export.impl;

import modules.export.Export;
import modules.export.Fragment;
import org.docx4j.XmlUtils;
import org.docx4j.dml.picture.Pic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.dml.wordprocessingDrawing.STAlignH;
import org.docx4j.dml.wordprocessingDrawing.STRelFromH;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.vml.*;
import org.docx4j.wml.*;
import org.docx4j.wml.CTBackground;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Bendikt Linke on 12.12.2015.
 */
public class DocxExport implements Export {

    String path;
    String fileName;

    WordprocessingMLPackage wordMLPackage;
    MainDocumentPart mainDocumentPart;


    @Override
    public void initialize(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;

        try {
            wordMLPackage = WordprocessingMLPackage.createPackage();
            mainDocumentPart = wordMLPackage.getMainDocumentPart();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(Fragment fragment) {
        double startX = fragment.getStartX() * 10;
        double startY = fragment.getStartY() * 10;
        double width = (fragment.getEndX() - fragment.getStartX()) * 10;
        double height = (fragment.getEndY() - fragment.getStartY()) * 10;


        String style = "position:absolute;" +
                "mso-wrap-style:square;" +
                "mso-width-percent:"+ width + ";" +
                "mso-height-percent:"+ height + ";" +
                "mso-left-percent:"+ startX + ";" +
                "mso-top-percent:"+ startY + ";";

        P p = new P();
        mainDocumentPart.getContent().add(p);


        if(fragment.getContent() instanceof String) {
            R r = Context.getWmlObjectFactory().createR();
            r.getContent().add(createTextBox(style, createContent(((String) fragment.getContent()))));
            p.getContent().add(r);
        }else{
            R r = Context.getWmlObjectFactory().createR();
            try {
                r.getContent().add(createTextBox(style, createImageInTextBox((BufferedImage)fragment.getContent())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            p.getContent().add(r);
        }
    }

    @Override
    public File finish() {
        File file = new File(path + fileName + ".docx");

        try {
            wordMLPackage.save(file);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

        return file;
    }

    // Create a TextBox inside a Picture
    private JAXBElement createTextBox(String style, P textboxContent) {
        org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();

        Pict pict = wmlObjectFactory.createPict();
        JAXBElement<org.docx4j.wml.Pict> pictWrapped = wmlObjectFactory.createRPict(pict);
        org.docx4j.vml.ObjectFactory vmlObjectFactory = new org.docx4j.vml.ObjectFactory();

        // Create object for shapetype (wrapped in JAXBElement)
        CTShapetype shapetype = vmlObjectFactory.createCTShapetype();
        JAXBElement<org.docx4j.vml.CTShapetype> shapetypeWrapped = vmlObjectFactory.createShapetype(shapetype);
        pict.getAnyAndAny().add( shapetypeWrapped);

        //define a textfield instead of autoform
        shapetype.setInsetmode(org.docx4j.vml.officedrawing.STInsetMode.CUSTOM);
        shapetype.setSpt( new Float(202.0) );
        shapetype.setConnectortype(org.docx4j.vml.officedrawing.STConnectorType.STRAIGHT);

        // Create object for path (wrapped in JAXBElement)
        CTPath path = vmlObjectFactory.createCTPath();
        JAXBElement<org.docx4j.vml.CTPath> pathWrapped = vmlObjectFactory.createPath(path);
        shapetype.getEGShapeElements().add( pathWrapped);
        path.setGradientshapeok(org.docx4j.vml.STTrueFalse.T);
        path.setConnecttype(org.docx4j.vml.officedrawing.STConnectType.RECT);
        shapetype.setCoordsize( "21600,21600");
        shapetype.setVmlId( "_x0000_t202");
        shapetype.setHralign(org.docx4j.vml.officedrawing.STHrAlign.LEFT);
        shapetype.setPath( "m,l,21600r21600,l21600,xe");

        // Create object for shape (wrapped in JAXBElement)
        CTShape shape = vmlObjectFactory.createCTShape();
        JAXBElement<org.docx4j.vml.CTShape> shapeWrapped = vmlObjectFactory.createShape(shape);

        pict.getAnyAndAny().add( shapeWrapped);

        shape.setStyle( style);
        shape.setSpid( "_x0000_s1026");

        // Remove the Stroke around textfield
        shape.setStroked(STTrueFalse.FALSE);
        shape.setFilled(STTrueFalse.FALSE);

        // Create object for textbox (wrapped in JAXBElement)
        CTTextbox textbox = vmlObjectFactory.createCTTextbox();
        JAXBElement<org.docx4j.vml.CTTextbox> textboxWrapped = vmlObjectFactory.createTextbox(textbox);
        shape.getEGShapeElements().add( textboxWrapped);
        textbox.setStyle( style);
        textbox.setInsetmode(org.docx4j.vml.officedrawing.STInsetMode.CUSTOM);
        // Create object for txbxContent
        CTTxbxContent txbxcontent = wmlObjectFactory.createCTTxbxContent();
        textbox.setTxbxContent(txbxcontent);

        txbxcontent.getContent().add(textboxContent);


        shape.setVmlId("TextBox");
        shape.setHralign(org.docx4j.vml.officedrawing.STHrAlign.LEFT);
        shape.setType( "#_x0000_t202");
        // <w10:wrap type="topAndBottom"/>
        return pictWrapped;
    }

    private P createContent(String textContent) {

        P p = Context.getWmlObjectFactory().createP();

        R r = Context.getWmlObjectFactory().createR();
        p.getContent().add( r);
        // Create object for t (wrapped in JAXBElement)
        Text text = Context.getWmlObjectFactory().createText();
        JAXBElement<org.docx4j.wml.Text> textWrapped = Context.getWmlObjectFactory().createRT(text);
        r.getContent().add( textWrapped);
        text.setValue( textContent);

        return p;
    }

    private P createImageInTextBox(BufferedImage content) throws Exception {

        P p = Context.getWmlObjectFactory().createP();

        String filenameHint = null;
        String altText = null;
        int id1 = 0;
        int id2 = 1;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes;
        ImageIO.write(content, "png", baos);
        imageBytes = baos.toByteArray();

        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageBytes);
        Inline inline = imagePart.createImageInline( filenameHint, altText, id1, id2, false);

        // Now add the inline in w:p/w:r/w:drawing
        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

        R  run = factory.createR();
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        p.getContent().add(run);

        return p;
    }

    // Create image, without specifying width
    private R newImage(byte[] bytes, String filenameHint, String altText, int id1, int id2) throws Exception {

        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
        Inline inline = imagePart.createImageInline( filenameHint, altText, id1, id2, false);


        // Now add the inline in w:p/w:r/w:drawing
        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

        org.docx4j.wml.R  run = factory.createR();
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return run;

    }
}
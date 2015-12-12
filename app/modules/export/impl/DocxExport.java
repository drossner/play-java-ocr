package modules.export.impl;

import modules.export.Export;
import modules.export.Fragment;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.vml.*;
import org.docx4j.vml.ObjectFactory;
import org.docx4j.vml.wordprocessingDrawing.CTWrap;
import org.docx4j.wml.*;
import play.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;

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


        // Relative position is done in percentages..

        //  mso-left-percent:600
        //  mso-position-horizontal-relative:left-margin-area
        //  mso-width-relative:margin

        // style="position:absolute;margin-left:0;margin-top:0;width:186.95pt;height:110.55pt;z-index:251659264;visibility:visible;mso-wrap-style:square;mso-width-percent:400;mso-height-percent:200;mso-left-percent:600;mso-wrap-distance-left:9pt;mso-wrap-distance-top:0;mso-wrap-distance-right:9pt;mso-wrap-distance-bottom:0;mso-position-horizontal-relative:left-margin-area;mso-position-vertical:absolute;mso-position-vertical-relative:text;mso-width-percent:400;mso-height-percent:200;mso-left-percent:600;mso-width-relative:margin;mso-height-relative:margin;v-text-anchor:top"


        // Absolute position to the right of column produces:

        // margin-left:108pt
        // mso-position-horizontal:absolute <------------
        // mso-position-horizontal-relative:text
        // mso-wrap-style:square

        // style="position:absolute;margin-left:108pt;margin-top:0;width:186.95pt;height:110.55pt;z-index:251659264;visibility:visible;mso-wrap-style:square;mso-width-percent:400;mso-height-percent:200;mso-wrap-distance-left:9pt;mso-wrap-distance-top:0;mso-wrap-distance-right:9pt;mso-wrap-distance-bottom:0;mso-position-horizontal:absolute;mso-position-horizontal-relative:text;mso-position-vertical:absolute;mso-position-vertical-relative:text;mso-width-percent:400;mso-height-percent:200;mso-width-relative:margin;mso-height-relative:margin;v-text-anchor:top"


        String style = "position:absolute;" +
                "z-index:251659264;" +
                "visibility:visible;" +
                "mso-wrap-style:square;" +
                "mso-width-percent:200;" + //length
                "mso-height-percent:0;" +
                "mso-left-percent:500;" + //postion startx
                "mso-top-percent:500;" + //postion starty
                "mso-wrap-distance-left: 9pt;" +
                "mso-wrap-distance-top:0;" +
                "mso-wrap-distance-right:9pt;" +
                "mso-wrap-distance-bottom:0;" +
                "v-text-anchor:top";

        P p = new P();
        mainDocumentPart.getContent().add(p);

        R r = Context.getWmlObjectFactory().createR();
        r.getContent().add(createPict(style , createContent(fragment.getContent())));
        p.getContent().add(r);
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

    private JAXBElement createPict(String style, P textboxContent) {

        org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();

        Pict pict = wmlObjectFactory.createPict();
        JAXBElement<org.docx4j.wml.Pict> pictWrapped = wmlObjectFactory.createRPict(pict);
        org.docx4j.vml.ObjectFactory vmlObjectFactory = new org.docx4j.vml.ObjectFactory();

        // Create object for shapetype (wrapped in JAXBElement)
        CTShapetype shapetype = vmlObjectFactory.createCTShapetype();
        JAXBElement<org.docx4j.vml.CTShapetype> shapetypeWrapped = vmlObjectFactory.createShapetype(shapetype);
        pict.getAnyAndAny().add( shapetypeWrapped);
        shapetype.setInsetmode(org.docx4j.vml.officedrawing.STInsetMode.CUSTOM);
        shapetype.setSpt( new Float(202.0) );
        shapetype.setConnectortype(org.docx4j.vml.officedrawing.STConnectorType.STRAIGHT);
        // Create object for stroke (wrapped in JAXBElement)
        CTStroke stroke = vmlObjectFactory.createCTStroke();
        JAXBElement<org.docx4j.vml.CTStroke> strokeWrapped = vmlObjectFactory.createStroke(stroke);
        shapetype.getEGShapeElements().add( strokeWrapped);
        stroke.setJoinstyle(org.docx4j.vml.STStrokeJoinStyle.MITER);
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
        shape.setInsetmode(org.docx4j.vml.officedrawing.STInsetMode.CUSTOM);
        shape.setConnectortype(org.docx4j.vml.officedrawing.STConnectorType.STRAIGHT);

        // Create object for textbox (wrapped in JAXBElement)
        CTTextbox textbox = vmlObjectFactory.createCTTextbox();
        JAXBElement<org.docx4j.vml.CTTextbox> textboxWrapped = vmlObjectFactory.createTextbox(textbox);
        shape.getPathOrFormulasOrHandles().add( textboxWrapped);
        textbox.setStyle( "mso-fit-shape-to-text:t");
        textbox.setInsetmode(org.docx4j.vml.officedrawing.STInsetMode.CUSTOM);
        // Create object for txbxContent
        CTTxbxContent txbxcontent = wmlObjectFactory.createCTTxbxContent();
        textbox.setTxbxContent(txbxcontent);

        txbxcontent.getContent().add( textboxContent);


        shape.setVmlId( "Text Box 2");
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
}

package modules.export.impl;

import modules.export.Export;
import modules.export.Fragment;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.*;
import java.util.List;

/**
 * Created by FRudi on 10.12.2015.
 */
public class PdfExport implements Export {

    String path;
    String fileName;
    Export export;

    @Override
    public void initialize(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;

        export = new DocxExport();
        export.initialize(path, fileName);
    }

    @Override
    public void export(Fragment fragment) {
        export.export(fragment);
    }

    @Override
    public void newPage() {
        export.newPage();
    }

    @Override
    public File finish() {

        File file = export.finish();
        createPDF(file);



        return file;
    }


    private void createPDF(File file) {
        try {
            String inputFilepath = path;

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);

            // Refresh the values of DOCPROPERTY fields
            FieldUpdater updater = new FieldUpdater(wordMLPackage);
            updater.update(true);

            // Set up font mapper (optional)
            Mapper fontMapper = new IdentityPlusMapper();
            wordMLPackage.setFontMapper(fontMapper);


            String regex = null;
            // Windows:
            // String
            // regex=".*(calibri|camb|cour|arial|symb|times|Times|zapf).*";
            //regex=".*(calibri|camb|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
            // Mac
            // String
             regex=".*(Courier New|Arial|Times New Roman|Comic Sans|Georgia|Impact|Lucida Console|Lucida Sans Unicode|Palatino Linotype|Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*";
            PhysicalFonts.setRegex(regex);

            PhysicalFont font = PhysicalFonts.get("Times New Roman");

            // FO exporter setup (required)
            // .. the FOSettings object
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setFoDumpFile(new java.io.File(inputFilepath + ".fo"));
            foSettings.setWmlPackage(wordMLPackage);


            String outputfilepath = inputFilepath + fileName + ".pdf";

            OutputStream os = new java.io.FileOutputStream(outputfilepath);

            // Don't care what type of exporter you use
            Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

            System.out.println("Saved: " + outputfilepath);

            // Clean up, so any ObfuscatedFontPart temp files can be deleted
            if (wordMLPackage.getMainDocumentPart().getFontTablePart()!=null) {
                wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
            }
            // This would also do it, via finalize() methods
            updater = null;
            foSettings = null;
            wordMLPackage = null;


        } catch (Docx4JException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void test(File file) {
        try {


            long start = System.currentTimeMillis();

// 1) Load DOCX into WordprocessingMLPackage

            InputStream is = new FileInputStream(file);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);


//if you want use any Physical fonts then use the below code.

            Mapper fontMapper = new IdentityPlusMapper();

            PhysicalFont font = PhysicalFonts.getPhysicalFonts().get("Comic Sans MS");

            fontMapper.getFontMappings().put("Algerian", font);

            wordMLPackage.setFontMapper(fontMapper);

// 2) Prepare Pdf settings

            PdfSettings pdfSettings = new PdfSettings();

// 3) Convert WordprocessingMLPackage to Pdf

            org.docx4j.convert.out.pdf.PdfConversion conversion = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wordMLPackage);

            OutputStream out = new FileOutputStream(new File(path+ "test.pdf"));
            conversion.output(out,pdfSettings);
            System.err.println("Time taken to Generate pdf  "+ (System.currentTimeMillis() - start) + "ms");
        } catch (Throwable e) {

            e.printStackTrace();
        }
    }
}

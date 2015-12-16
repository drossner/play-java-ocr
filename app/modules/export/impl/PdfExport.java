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

/**
 * Created by FRudi on 10.12.2015.
 */
public class PdfExport implements Export {

    Export export;

    @Override
    public void initialize(String path, String fileName) {
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
        return null;
    }


    private void createPDF(File file) {

    }
}

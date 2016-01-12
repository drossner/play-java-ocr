package modules.export;

import modules.export.impl.DocxExport;
import modules.export.impl.OdtExport;
import modules.export.impl.PdfExport;

/**
 * Created by daniel and benedikt on 21.12.15.
 * Class for initializing a document exporter
 */
public class ExporterFactory {

    private static String[] types = {"docx", "pdf", "odt"};

    public static String[] getImpls(){
        return types;
    }

    /**
     *
     * @param dataType file extension
     * @return Specific exporter
     */
    public static Export getExporter(String dataType){
        dataType = dataType.toLowerCase();
        Export rc;
        if(dataType.equals(types[0])) rc = new DocxExport();
        else if(dataType.equals(types[1])) rc = new PdfExport();
        else if(dataType.equals(types[2])) rc = new OdtExport();
        else {
            rc = null;
        }

        return rc;
    }

}

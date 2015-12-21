package modules.analyse;

import com.fasterxml.jackson.databind.ObjectMapper;
import control.result.Result;
import control.result.ResultFragment;
import control.result.Type;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.export.Export;
import play.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Benedikt Linke on 21.12.15.
 */
public class AnalyseExport {

    public File getExportFile (Export export, String docid, String name){
        CMSController cmsController = SessionHolder.getInstance().getController("ocr", "ocr");
        ObjectMapper mapper = new ObjectMapper();
        Logger.info("name: " + name);

        export.initialize("",name, false);

        Result result;
        try {
            result = mapper.readValue(cmsController.readingJSON(docid), Result.class);
        } catch (IOException e) {
            e.printStackTrace();
            result = new Result();
        }


        for(ResultFragment fragment: result.getResultFragments()){
            String imageID;
            if(fragment.getType() == Type.IMAGE){
                imageID = (String) fragment.getResult();

                fragment.setResult(cmsController.readingAImage((String) fragment.getResult()));
                export.export(fragment);

                fragment.setResult(imageID);
            }else if(fragment.getType() != Type.PAGEBREAK){
                export.export(fragment);
            }else{
                export.newPage();
            }
        }

        return export.finish();
    }
}

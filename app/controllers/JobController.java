package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import modules.analyse.Analyse;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.entities.Job;
import modules.upload.ImageHelper;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.impl.Base64;
import org.imgscalr.Scalr;
import play.api.Play;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.Controller;
import play.Logger;
import play.mvc.Result;
import play.libs.Json;
import views.html.ablage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by florian on 29.11.15.
 */
public class JobController extends Controller {

    public Result getJobHistory(){
        List<Job> jobs = null;
        String username = session().get("session");

        try {
            jobs = new modules.database.JobController().getUnProcessedJobs();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        /*
        Job job = new Job();
        job.setName("test.png");
        job.setId(18);
        jobs.add(job);

        job = new Job();
        job.setName("test2.png");
        job.setId(22);
        jobs.add(job);

        job = new Job();
        job.setName("test3.png");
        job.setId(38);
        jobs.add(job);
        */

        if(jobs == null){
            return ok(Json.toJson(new ArrayList<Job>()));
        }else{
            return ok(Json.toJson(jobs));
        }
    }

    public Result getJobTypes(){
        ArrayList<String> rc = new ArrayList<>();
        String username = session().get("session");

        //TODO select from database

        rc.add("Rechnung");
        rc.add("Dies");
        rc.add("und");
        rc.add("das");

        return ok(Json.toJson(rc));
    }

    public Result getLanguages() throws Throwable {
        /*ArrayList<String> rc = new ArrayList<>();
        String username = session().get("session");

        rc.add("Detusch");
        rc.add("Anglisch");
        rc.add("Schwiezerdütsch");
        rc.add("Fränggisch"); */

        modules.database.JobController controller = new modules.database.JobController();

        return ok(Json.toJson(controller.getAllCountryLanguages()));
    }

    public Result getImageFromJobID(int id) throws IOException {
        Logger.info("id erhalten: " + id);

        Job job = null;
        try {
            job = new modules.database.JobController().getJobById(id);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if(job != null){
            Logger.info("job erhalten");

            //TODO geht irgendwie net ^^
            /*
            if(session().get("session").trim().equals(job.getUser().geteMail().trim())){
                Logger.info("session not user email => " + session().get("session") + " != " + job.getUser().geteMail());
                //TODO DANIEL ERRROR
                return internalServerError();
            }*/

            CMSController controller = SessionHolder.getInstance().getController("ocr", "ocr");

            BufferedImage image = controller.readingAImage(job.getImage().getSource());
            image = new ImageHelper().scale(image, 536, 0);

            Logger.info("image: " + image);

            return ok(new ImageHelper().convertBaos(image).toByteArray()).as("image/jpeg");
        }
        //TODO DANIEL ERROR
        return internalServerError();
        /*
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        input = new ByteArrayInputStream(bFile);

        return ok(input).as("image/png");
        *//*
        File file = new File(job.getImage().getSource());

        Logger.info("returning: " + file);
        return ok(Json.toJson(file));*/
    }

    public Result delete(int id){
        return ok();
    }

    public F.Promise<Result> process(){
        return F.Promise.promise(() -> {
            JsonNode jobs = request().body().asJson();
            Logger.info(jobs.toString());

            for (JsonNode node : jobs.withArray("jobs")) {
                JsonNode job = node.get("job");

                Analyse.INSTANCE.calculate(job);
            }

            return ok(ablage.render());
        });
    }
}

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import modules.database.entities.Job;
import play.mvc.Controller;
import play.Logger;
import play.mvc.Result;
import play.libs.Json;

import java.io.File;
import java.util.ArrayList;
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

    public Result getImageFromJobID(int id){
        Logger.info("id erhalten: " + id);

        Job job = null;
        try {
            job = new modules.database.JobController().getJobById(id);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        File file = new File(job.getImage().getSource());

        Logger.info("returning: " + file);
        return ok(file.getAbsolutePath());
        //TODO ask daniel! return new UploadController(null, null).getFile("1", file.getAbsolutePath());
    }

    public Result process(){
        Logger.info(request().toString());

        JsonNode jobs = request().body().asJson();

        Logger.info(jobs.toString());

        return ok();
    }
}

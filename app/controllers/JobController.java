package controllers;

import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.OcrDeadboltHandler;
import modules.analyse.Analyse;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.*;
import modules.database.UserController;
import modules.database.entities.Job;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.libs.F;
import util.ImageHelper;
import play.mvc.Controller;
import play.Logger;
import play.mvc.Result;
import play.libs.Json;
import views.html.ablage;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by florian and daniel on 29.11.15.
 */
public class JobController extends Controller {

    private ImageHelper imageHelper;

    @Inject
    public JobController(ImageHelper imageHelper){
        this.imageHelper = imageHelper;
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result getJobHistory(){
        List<Job> jobs = null;
        String username = session().get("session");

        try {
            jobs = new modules.database.JobController().getUnProcessedJobs(username);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if(jobs == null){
            return ok(Json.toJson(new ArrayList<Job>()));
        }else{
            return ok(Json.toJson(jobs));
        }
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
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

    @SubjectPresent
    public Result getLanguages() throws Throwable {
        modules.database.JobController controller = new modules.database.JobController();

        return ok(Json.toJson(controller.getAllCountryLanguages()));
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
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
            image = imageHelper.scale(image, ImageHelper.TEMPLATE_WIDTH, 0);

            return ok(imageHelper.convertBaos(image).toByteArray()).as(ImageHelper.OUTPUT_MIMETYPE);
        }
        //TODO DANIEL ERROR
        return internalServerError();
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> delete(int id){
        String userEmail = session().get("session");
        return F.Promise.promise(() -> JPA.withTransaction(() -> {
            modules.database.JobController controller = new modules.database.JobController();
            Job job = controller.selectEntity(Job.class, "id", id);

            if(!job.getUser().geteMail().equals(userEmail)){
                return badRequest();
            }

            controller.deleteObject(job);

            return ok();
        }));
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> process(){
        return F.Promise.promise(() -> {
            JsonNode jobs = request().body().asJson();
            Logger.info(jobs.toString());

            Analyse.INSTANCE.analyse(jobs);

            return ok();
        });
    }

    /* _____________________________________
        Ablage
       _____________________________________ */

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> getProcessedJobs(){
        String username = session().get("session");

        return F.Promise.promise(() -> {
            //init root node
            ObjectNode result = Json.newObject();
            //add export filetypes TODO: dynamic loading
            result.putArray("filetypes").add("docx").add("pdf").add("txt");
            //init nodes array
            ArrayNode arrayNode = result.putArray("nodes");

            //init JSON mapper for working with result texts from cmis
            ObjectMapper mapper = new ObjectMapper();
            //load current user from database
            User user = JPA.withTransaction(() -> new UserController().selectUserFromMail(username));

            //load finished job data from database (for current user)
            List<Job> jobs = JPA.withTransaction(() -> {
                ArrayList<String> whereColumn = new ArrayList<>();
                whereColumn.add("user");
                whereColumn.add("processed");

                ArrayList<Object> whereValue = new ArrayList<>();
                whereValue.add(user);
                whereValue.add(true);

                return new modules.database.JobController().selectEntityList(Job.class, whereColumn, whereValue);
            });

            //init cms controller to load result json form cmis
            CMSController cmsController = SessionHolder.getInstance().
                    getController("ocr", "ocr");

            //result of analyse part
            ArrayList<control.result.Result> rc = new ArrayList<>();

            //iterate over all availabe jobs and build result json for the client
            for(Job job: jobs){
                control.result.Result tempResult = mapper.readValue(cmsController.readingJSON(job.getResultFile()), control.result.Result.class);
                String name = job.getName();
                String language = "Deutsch";//job.getLayoutConfig().getLanguage().getCountry().getName();
                String type = "Rechnung"; //job.getLayoutConfig().getName();
                //TODO: add texts
                addObjectToArray(arrayNode, name, language, type);
            }

            return ok(result);
        });
    }

    private void addObjectToArray(ArrayNode array, String name,
                                  String language, String type){
        array.addObject()
                .put("name", name)
                .put("language", language)
                .put("type", type);
    }
}

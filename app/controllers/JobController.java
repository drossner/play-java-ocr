package controllers;

import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.java.actions.Pattern;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import control.result.ResultFragment;
import control.result.Type;
import controllers.security.OcrDeadboltHandler;
import modules.analyse.Analyse;
import modules.analyse.AnalyseExport;
import modules.cms.*;
import modules.cms.data.FileType;
import modules.database.*;
import modules.database.UserController;
import modules.database.entities.*;
import modules.export.Export;
import modules.export.ExporterFactory;
import modules.export.impl.DocxExport;
import org.apache.chemistry.opencmis.client.api.Document;
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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * Created by florian and daniel on 29.11.15.
 */
public class JobController extends Controller {

    private ImageHelper imageHelper;
    private ConcurrentSkipListMap<String, File> downloadMap;

    @Inject
    public JobController(ImageHelper imageHelper){
        this.imageHelper = imageHelper;
        this.downloadMap = new ConcurrentSkipListMap<>();
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result getJobHistory(String uploadID){
        List<Job> jobs = null;
        String username = session().get("session");

        try {
            jobs = new modules.database.JobController().getUnProcessedJobs(uploadID, username);
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
        ArrayList<LayoutArea> rc = new ArrayList<>();
        String username = session().get("session");

        User user;
        try {
            user = JPA.withTransaction(() -> new UserController().selectUserFromMail(username));
        } catch (Throwable throwable) {
            user = null;
            throwable.printStackTrace();
        }

        final User finalUser = user;
        JPA.withTransaction(() -> {
            ArrayList<LayoutConfig> configs = new ArrayList<>();
            modules.database.LayoutConfigurationController controller = new modules.database.LayoutConfigurationController();
            LayoutFragmentController fragmentController = new LayoutFragmentController();

            List<LayoutConfig> tempLayoutConfigs = controller.selectEntityList(LayoutConfig.class, finalUser);
            if(tempLayoutConfigs != null && tempLayoutConfigs.size() > 0){
                configs.addAll(tempLayoutConfigs);
            }
            configs.addAll(controller.selectEntityListColumnNull("user"));

            for(LayoutConfig config : configs){
                LayoutArea temp = new LayoutArea();

                temp.config = config;
                temp.fragments = fragmentController.selectEntityList(LayoutFragment.class, "layoutConfig", config.getId());

                rc.add(temp);
            }
        });

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
        String username = session().get("session");
        return F.Promise.promise(() -> {
            JsonNode jobs = request().body().asJson();
            Logger.info(jobs.toString());

            Analyse.INSTANCE.analyse(jobs, username);

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
            ArrayNode tempArrNode = result.putArray("filetypes");
            for(String type : ExporterFactory.getImpls()){
                tempArrNode.add(type);
            }
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
                int id = job.getId();
                String name = job.getName();
                String language = CountryImpl.GERMAN.getName();//job.getLayoutConfig().getLanguage().getCountry().getName();
                String type = ""; //job.getLayoutConfig().getName();
                if(job.getLayoutConfig() != null){
                    language = job.getLayoutConfig().getLanguage().getCountry().getName();
                    type = job.getLayoutConfig().getName();
                }
                ArrayList<String> resultFragments = new ArrayList<String>();
                for (ResultFragment fragment: tempResult.getResultFragments()){
                    if(fragment.getType() == Type.TEXT){
                        resultFragments.add(mapper.writeValueAsString(fragment));
                    }
                }
                addObjectToArray(arrayNode, id, name, language, type, resultFragments);
            }

            return ok(result);
        });
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> saveFragments() {
        Logger.debug("FragmentSave requested");
        JsonNode jsondata = request().body().asJson();
        Logger.debug(jsondata.toString());
        return F.Promise.promise(() -> {
            //extract data from json
            int jobid = jsondata.get("id").asInt();
            List<String> fragments = new ArrayList<>();
            for (final JsonNode data : jsondata.get("fragments")) {
                fragments.add(data.get("fragment").textValue());
            }
            //load current user from database
            User user = JPA.withTransaction(() -> new UserController().selectUserFromMail(session().get("session")));
            //load finished job data from database (for current user)
            List<Job> jobs = JPA.withTransaction(() -> {
                ArrayList<String> whereColumn = new ArrayList<>();
                whereColumn.add("id");
                whereColumn.add("user");

                ArrayList<Object> whereValue = new ArrayList<>();
                whereValue.add(jobid);
                whereValue.add(user);

                return new modules.database.JobController().selectEntityList(Job.class, whereColumn, whereValue);
            });
            Iterator<Job> jobsIt = jobs.iterator();
            if(!jobsIt.hasNext()) return badRequest();

            //init cms controller to load result json form cmis
            CMSController cmsController = SessionHolder.getInstance().
                    getController("ocr", "ocr");
            modules.cms.FolderController folderController = new modules.cms.FolderController(cmsController);
            //init JSON mapper for working with result texts from cmis
            ObjectMapper mapper = new ObjectMapper();
            Job job = jobsIt.next();
            control.result.Result tempResult = mapper.readValue(cmsController.readingJSON(job.getResultFile()), control.result.Result.class);
            cmsController.deleteDocument(job.getResultFile());

            File file = File.createTempFile("result"+job.getUser().geteMail(), ".json");
            //new fragments from client
            Iterator<String> fIt = fragments.iterator();
            for (ResultFragment fragment: tempResult.getResultFragments()){
                if(fragment.getType() == Type.TEXT){
                    fragment.setResult(fIt.next());
                }
            }
            mapper.writeValue(file, tempResult);
            Document doc = cmsController.createDocument(folderController.getUserWorkspaceFolder(), file, FileType.FILE.getType());
            JPA.withTransaction(() -> {
                Job tempJob = new modules.database.JobController().selectEntity(Job.class, "id", jobid);
                tempJob.setResultFile(doc.getId());
            });
            return ok();
        });
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> shareDocument(String folderid, int id, String ext){
        String userEmail = session().get("session");

        return F.Promise.promise(() -> JPA.withTransaction(() -> {
            if(folderid == null || folderid.equals("")){
                return badRequest();
            }

            modules.database.JobController controller = new modules.database.JobController();
            Job job = controller.selectEntity(Job.class, "id", id);
            CMSController cmsController;

            if(!job.getUser().geteMail().equals(userEmail)){
                return unauthorized();
            }

            cmsController = SessionHolder.getInstance().getController(job.getUser().getCmsAccount(), job.getUser().getCmsPassword());

            File file = new AnalyseExport().getExportFile(ExporterFactory.getExporter(ext), job.getResultFile(), job.getName());
            cmsController.createDocument(folderid, file, FileType.FILE.getType());

            return ok();
        }));
    }

    public  F.Promise<Result> getDownloadlink(int id, String ext) {
        Logger.debug("Downloadlink requested");
        ObjectNode result = Json.newObject();

        return F.Promise.promise(() -> {
            modules.database.JobController jobController = new modules.database.JobController();
            Job job = JPA.withTransaction(() -> jobController.selectEntity(Job.class, "id", id));
            AnalyseExport ae = new AnalyseExport();
            Export exporter = ExporterFactory.getExporter(ext);
            if(exporter == null) return badRequest();
            File toDownload = ae.getExportFile(exporter, job.getResultFile(), job.getName());
            String filekey = addFileToMap(toDownload);
            result.put("url", routes.JobController.downloadFile(filekey).url());
            return ok(result);
        });
    }

    public Result downloadFile(String fileid) {
        File f = downloadMap.get(fileid);
        return (f==null) ? badRequest() : ok(f);
    }

    private void addObjectToArray(ArrayNode array, int id, String name,
                                  String language, String type, ArrayList<String> resultFragments){
        ArrayNode temp = array.addObject()
                .put("id", id)
                .put("name", name)
                .put("language", language)
                .put("type", type)
                .putArray("fragments");

        Iterator<String> it = resultFragments.iterator();
        while(it.hasNext()){
            String val = Json.parse(it.next()).get("result").asText();
            temp.add(val);
        }

                //.put("fragments", Json.toJson(resultFragments));
    }

    private String addFileToMap(File f){
        String uuid = UUID.randomUUID().toString();
        String key = uuid+"&"+System.currentTimeMillis();
        downloadMap.put(key, f);
        return key;
    }

    public class LayoutArea{
        public LayoutConfig config;

        public List<LayoutFragment> fragments;
    }
}

package modules.analyse;

import analyse.AnalyseType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import control.configuration.LayoutFragment;
import control.factories.LayoutConfigurationFactory;
import control.result.Result;
import control.result.ResultFragment;
import control.result.Type;
import errorhandling.OcrException;
import modules.cms.CMSController;
import modules.cms.FolderController;
import modules.cms.SessionHolder;
import modules.cms.data.FileType;
import modules.database.UserController;
import modules.database.entities.Job;
import modules.database.entities.User;
import modules.database.factory.SimpleLayoutConfigurationFactory;
import modules.export.Export;
import modules.export.impl.DocxExport;
import modules.export.impl.OdtExport;
import modules.export.impl.PdfExport;
import org.apache.chemistry.opencmis.client.api.Document;
import play.Logger;
import play.db.jpa.JPA;
import postprocessing.PostProcessingType;
import preprocessing.PreProcessingType;
import preprocessing.PreProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by FRudi on 17.12.2015.
 */
public enum Analyse {
    INSTANCE();

    private final int THREAD_POOL_COUNT = 4;

    //private ExecutionContext context;
    //private ExecutorService executor;

    private CMSController controller;
    private final FolderController folderController;

    Analyse() {
        //executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        controller = SessionHolder.getInstance().getController("ocr", "ocr");
        folderController = new FolderController(controller);
        //context = Akka.system().dispatcher().prepare();
    }

    public void analyse(JsonNode jobs, String username) throws Throwable {
        Export export = new DocxExport();
        ObjectMapper mapper = new ObjectMapper();
        Result result = null;
        User user = JPA.withTransaction(() -> new UserController().selectUserFromMail(username));

        if(jobs.get("combined").asBoolean()){
            Result temp = null;
            ArrayList<ResultFragment> fragments = new ArrayList<>();
            String name = null;
            String folderId = null;

            for (JsonNode node : jobs.withArray("jobs")) {
                if(name == null){
                    name = node.get("job").get("name").asText().split("\\.")[0];
                }
                if(folderId == null){
                    folderId = node.get("folderId").asText();
                }
                temp = calculate(node);
                fragments.addAll(temp.getResultFragments());

                ResultFragment pageBreak = new ResultFragment();
                pageBreak.setType(Type.PAGEBREAK);
                fragments.add(pageBreak);
            }

            export.initialize(folderId, name, false);

            String imageID;
            for(ResultFragment fragment: fragments){
                if(fragment.getType() == Type.IMAGE){
                    imageID = (String) fragment.getResult();

                    fragment.setResult(controller.readingAImage((String) fragment.getResult()));
                    export.export(fragment);

                    fragment.setResult(imageID);
                }else if(fragment.getType() != Type.PAGEBREAK){
                    export.export(fragment);
                }else{
                    export.newPage();
                }
            }

            final String finalFolderId = folderId;
            SessionHolder.getInstance().getController(user.getCmsAccount(), user.getCmsPassword())
                    .createDocument(finalFolderId, export.finish(), FileType.FILE.getType());

            temp.setResultFragments(fragments);
            result = temp;
        }else{
            for(JsonNode node : jobs.withArray("jobs")){
                String name = node.get("job").get("name").asText().split("\\.")[0];
                String folderId = node.get("folderId").asText();

                Logger.info("name: " + name);
                Logger.info("folderid: " + folderId);

                export.initialize(folderId, name, false);

                result = calculate(node);

                result.getResultFragments().stream().filter(fragment -> fragment.getType() == Type.IMAGE).forEach(fragment -> {
                    fragment.setResult(controller.readingAImage((String) fragment.getResult()));
                });

                result.getResultFragments().forEach(export::export);

                SessionHolder.getInstance().getController(user.getCmsAccount(), user.getCmsPassword())
                            .createDocument(folderId, export.finish(), FileType.FILE.getType());
            }
        }


        File file = new File("./job_" + user.geteMail() + "_" + new Date() + ".json");

        try {
            mapper.writeValue(file, result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String idString = jobs.get("jobs").get("job").get("id").asText();
        int jobID = Integer.parseInt(idString);
        try {
            Document doc = controller.createDocument(folderController.getUserWorkspaceFolder(), file, FileType.FILE.getType());

            JPA.withTransaction(() -> {
                Job job = new modules.database.JobController().selectEntity(Job.class, "id", jobID);
                job.setResultFile(doc.getId());

                job.setProcessed(true);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            JPA.withTransaction(() -> {
                Job job = new modules.database.JobController().selectEntity(Job.class, "id", jobID);
                job.setResultFile("error! " + Arrays.toString(e.getStackTrace()));

                job.setProcessed(false);
            });
        }

        Logger.info("node complete processed");
    }

    private Result calculate(JsonNode job) {
        Logger.info("analyse job: " + job);
        AnalyseWorker worker = new AnalyseWorker();
        LayoutConfigurationFactory configuration = new LayoutConfigurationFactory();

        JsonNode preProcessor = job.get("preProcessing");
        JsonNode areas = job.get("areas");

        String name = null;
        if(job.get("templateName") != null){
            name = job.get("templateName").textValue();
        }
        SimpleLayoutConfigurationFactory dbConfigurationFactory = null;
        if(name != null && !name.equals("")) {
            dbConfigurationFactory = new SimpleLayoutConfigurationFactory();
        }

        Job dbJob;

        BufferedImage image;
        try {
            String idString = job.get("job").get("id").asText();
            int jobID = Integer.parseInt(idString);
            dbJob = JPA.withTransaction(() -> new modules.database.JobController().selectEntity(Job.class, "id", jobID));
            image = controller.readingAImage(dbJob.getImage().getSource());
        } catch (Throwable throwable) {
            throw new OcrException("Analyse", throwable);
        }

        for (JsonNode preProc : preProcessor) {
            PreProcessor pre = null;
            switch (preProc.get("type").textValue().toLowerCase()) {
                case "rotate":
                    pre = PreProcessingType.ROTATE;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    break;
                case "brightness":
                    pre = PreProcessingType.INCREASE_BRIGHTNESS;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    break;
                case "contrast":
                    pre = PreProcessingType.INCREASE_CONTRAST;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    break;
            }
            if(dbConfigurationFactory != null){
                dbConfigurationFactory.addPreProcessing(pre);
            }
        }

        for (JsonNode area : areas) {
            AnalyseType type;
            switch (area.get("type").textValue().toLowerCase()) {
                case "metadata":
                    type = AnalyseType.META_DATA_FRAGMENT;
                    break;
                case "img":
                    type = AnalyseType.IMAGE_FRAGMENT;
                    break;
                case "text":
                    type = AnalyseType.TEXT_FRAGMENT;
                    break;
                default:
                    type = AnalyseType.TEXT_FRAGMENT;
                    break;
            }

            double xStart = area.get("xStart").asDouble();
            double xEnd = area.get("xEnd").asDouble();
            double yStart = area.get("yStart").asDouble();
            double yEnd = area.get("yEnd").asDouble();

            Logger.info("xs: " + xStart + " ys: " + yStart + " xe: " + xEnd + " ye: " + yEnd);

            LayoutFragment fragment = new LayoutFragment(xStart, xEnd, yStart, yEnd, type);

            configuration.addLayoutFragment(fragment);
            if(dbConfigurationFactory != null){
                dbConfigurationFactory.addFragment(new SimpleLayoutConfigurationFactory().createLayoutFragmentFactory()
                        .setXEnd(xEnd)
                        .setXStart(xStart)
                        .setYEnd(yEnd)
                        .setYStart(yStart)
                        .setType(area.get("type").textValue().toLowerCase())
                        .build());
            }
        }

        configuration.addPostProcessor(PostProcessingType.TEXT_CHECK);

        final SimpleLayoutConfigurationFactory finalDbConfigurationFactory = dbConfigurationFactory;
        final String finalName = name;
        JPA.withTransaction(() -> {
            if(finalDbConfigurationFactory != null) {
                finalDbConfigurationFactory.addPostProcessing(PostProcessingType.TEXT_CHECK);
                finalDbConfigurationFactory.setUser(dbJob.getUser());
                finalDbConfigurationFactory.setName(finalName);

                dbJob.setLayoutConfig(finalDbConfigurationFactory.build());
            }else{
                dbJob.setLayoutConfig(null);
            }
        });


        worker.setImage(image);
        worker.setJob(dbJob);
        worker.setConfiguration(configuration.build());

        /*
        F.Promise<Integer> integerPromise = F.Promise.promise(worker::run
                , context);
                */

        Result rc = worker.run();

        return rc;
    }
}

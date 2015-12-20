package modules.analyse;

import analyse.AnalyseType;
import com.fasterxml.jackson.databind.JsonNode;
import control.configuration.LayoutFragment;
import control.factories.LayoutConfigurationFactory;
import control.result.Result;
import control.result.ResultFragment;
import errorhandling.OcrException;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.entities.Job;
import modules.database.factory.SimpleLayoutConfigurationFactory;
import modules.export.Export;
import modules.export.impl.DocxExport;
import modules.export.impl.PdfExport;
import play.Logger;
import play.db.jpa.JPA;
import postprocessing.PostProcessingType;
import preprocessing.PreProcessingType;
import preprocessing.PreProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by FRudi on 17.12.2015.
 */
public enum Analyse {
    INSTANCE();

    private final int THREAD_POOL_COUNT = 4;

    //private ExecutionContext context;
    //private ExecutorService executor;

    private CMSController controller;

    Analyse() {
        //executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        controller = SessionHolder.getInstance().getController("ocr", "ocr");
        //context = Akka.system().dispatcher().prepare();
    }

    public void analyse(JsonNode jobs){

        Export export = new PdfExport();

        if(jobs.get("combined").asBoolean()){
            ArrayList<Result> results = new ArrayList<>();
            for (JsonNode node : jobs.withArray("jobs")) {
                results.add(calculate(node));
            }

            export.initialize(jobs.get("jobs").get(1).get("job").get("folderId").asText(), jobs.get(1).get("job").get("name").asText(), false);

            for(Result result: results){
                result.getResultFragments().forEach(export::export);
                export.newPage();
            }

            export.finish();
        }else{
            for(JsonNode node : jobs.withArray("jobs")){
                String name = node.get("job").get("name").asText().split("\\.")[0];
                String folderId = node.get("folderId").asText();

                Logger.info("name: " + name);
                Logger.info("folderid: " + folderId);

                export.initialize(folderId, name, false);

                calculate(node).getResultFragments().forEach(export::export);

                export.finish();

                Logger.info("node complete processed");
            }

        }
    }

    private Result calculate(JsonNode job) {
        Logger.info("analyse job: " + job);
        AnalyseWorker worker = new AnalyseWorker();
        LayoutConfigurationFactory configuration = new LayoutConfigurationFactory();
        SimpleLayoutConfigurationFactory dbConfigurationFactory = new SimpleLayoutConfigurationFactory();
        Job dbJob;

        BufferedImage image;
        try {
            String idString = job.get("job").get("id").asText();
            int jobID = Integer.parseInt(idString);
            dbJob = JPA.withTransaction(() -> new modules.database.JobController().selectEntity(Job.class, "id", jobID));
            image = JPA.withTransaction(() -> controller.readingAImage(dbJob.getImage().getSource()));
        } catch (Throwable throwable) {
            throw new OcrException("Analyse", throwable);
        }

        JsonNode preProcessor = job.get("preProcessing");
        JsonNode areas = job.get("areas");

        for (JsonNode preProc : preProcessor) {
            switch (preProc.get("type").textValue().toLowerCase()) {
                case "rotate":
                    PreProcessor pre = PreProcessingType.ROTATE;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    dbConfigurationFactory.addPreProcessing(pre);
                    break;
                case "brightness":
                    pre = PreProcessingType.INCREASE_BRIGHTNESS;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    dbConfigurationFactory.addPreProcessing(pre);
                    break;
                case "contrast":
                    pre = PreProcessingType.INCREASE_CONTRAST;
                    pre.setValue(preProc.get("processValue").doubleValue());
                    configuration.addPreProcessor(pre);
                    dbConfigurationFactory.addPreProcessing(pre);
                    break;
            }
        }

        for (JsonNode area : areas) {
            AnalyseType type = null;
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
            dbConfigurationFactory.addFragment(new SimpleLayoutConfigurationFactory().createLayoutFragmentFactory()
                    .setXEnd(xEnd)
                    .setXStart(xStart)
                    .setYEnd(yEnd)
                    .setYStart(yStart)
                    .setType(area.get("type").textValue().toLowerCase())
                    .build());
        }

        JPA.withTransaction(() ->{
            dbConfigurationFactory.addPostProcessing(PostProcessingType.TEXT_CHECK);
            configuration.addPostProcessor(PostProcessingType.TEXT_CHECK);

            dbConfigurationFactory.setUser(dbJob.getUser());
            //String name = areas.get("name").textValue();
            String name = "test";
            dbConfigurationFactory.setName(name);

            dbJob.setLayoutConfig(dbConfigurationFactory.build());
        });

        worker.setImage(image);
        worker.setJob(dbJob);
        worker.setConfiguration(configuration.build());


        /*
        F.Promise<Integer> integerPromise = F.Promise.promise(worker::run
                , context);
                */

        Result rc = worker.run();

        JPA.withTransaction(() -> dbJob.setProcessed(true));

        return rc;
    }
}

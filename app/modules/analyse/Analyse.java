package modules.analyse;

import analyse.AnalyseType;
import com.fasterxml.jackson.databind.JsonNode;
import control.configuration.LayoutFragment;
import control.factories.LayoutConfigurationFactory;
import errorhandling.ErrorHandler;
import errorhandling.OcrException;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.entities.Job;
import modules.database.entities.PreProcessing;
import modules.database.factory.SimpleLayoutConfigurationFactory;
import play.Logger;
import play.api.UsefulException;
import play.db.jpa.JPA;
import postprocessing.PostProcessingType;
import preprocessing.PreProcessingType;
import preprocessing.PreProcessor;
import util.ImageHelper;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by FRudi on 17.12.2015.
 */
public enum Analyse {
    INSTANCE();

    private final int THREAD_POOL_COUNT = 4;

    private CMSController controller;
    private ExecutorService executor;

    Analyse() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        controller = SessionHolder.getInstance().getController("ocr", "ocr");
    }

    public void calculate(JsonNode job) {
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
            image = JPA.withTransaction(() -> {
                return controller.readingAImage(dbJob.getImage().getSource());
            });
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
                case "image":
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

        executor.execute(worker);

        JPA.withTransaction(() -> dbJob.setProcessed(true));
    }
}

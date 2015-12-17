package modules.analyse;

import analyse.AnalyseType;
import com.fasterxml.jackson.databind.JsonNode;
import control.configuration.LayoutConfiguration;
import control.configuration.LayoutFragment;
import control.factories.LayoutConfigurationFactory;
import modules.database.entities.Job;
import modules.database.factory.SimpleLayoutConfigurationFactory;
import modules.upload.ImageHelper;
import play.db.jpa.JPA;
import postprocessing.PostProcessingType;
import preprocessing.PreProcessingType;
import preprocessing.PreProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by FRudi on 17.12.2015.
 */
public enum Analyse {
    INSTANCE;

    private final int THREAD_POOL_COUNT = 4;

    private ExecutorService executor;

    Analyse(){
        executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
    }

    public void calculate(JsonNode job){
        JPA.withTransaction(() -> {
            AnalyseWorker worker = new AnalyseWorker();
            LayoutConfigurationFactory configuration = new LayoutConfigurationFactory();
            SimpleLayoutConfigurationFactory dbConfigurationFactory = new SimpleLayoutConfigurationFactory();
            Job dbJob = new modules.database.JobController().selectEntity(Job.class, "id", Integer.getInteger(job.get("id").textValue()));

            JsonNode prePocessor = job.get("preProcessing");
            JsonNode areas = job.get("areas");

            for(JsonNode preProc: prePocessor){
                switch(preProc.get("type").textValue().toLowerCase()){
                    case "rotate":
                        configuration.addPreProcessor(PreProcessingType.ROTATE);
                        dbConfigurationFactory.addPreProcessing(PreProcessingType.ROTATE);
                        break;
                    case "brightness":
                        configuration.addPreProcessor(PreProcessingType.ROTATE);
                        dbConfigurationFactory.addPreProcessing(PreProcessingType.ROTATE);
                        break;
                    case "contrast":
                        configuration.addPreProcessor(PreProcessingType.INCREASE_CONTRAST);
                        dbConfigurationFactory.addPreProcessing(PreProcessingType.INCREASE_CONTRAST);
                        break;
                }
            }

            for(JsonNode area: areas){
                AnalyseType type = null;
                switch(area.get("type").textValue().toLowerCase()){
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

                double xStart = Double.parseDouble(area.get("xStart").textValue());
                double xEnd = Double.parseDouble(area.get("xEnd").textValue());
                double yStart = Double.parseDouble(area.get("yStart").textValue());
                double yEnd = Double.parseDouble(area.get("yEnd").textValue());

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

            dbConfigurationFactory.addPostProcessing(PostProcessingType.TEXT_CHECK);
            configuration.addPostProcessor(PostProcessingType.TEXT_CHECK);

            dbConfigurationFactory.setUser(dbJob.getUser());
            dbConfigurationFactory.setName(areas.get("name").textValue());

            dbJob.setLayoutConfig(dbConfigurationFactory.build());

            worker.setImage(new ImageHelper().convertToImageFromCMIS(dbJob.getImage().getSource()));
            worker.setJob(dbJob);
            worker.setConfiguration(configuration.build());

            executor.execute(worker);

            dbJob.setProcessed(true);
        });

    }
}

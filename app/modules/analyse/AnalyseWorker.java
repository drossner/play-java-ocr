package modules.analyse;

import analyse.AnalyseType;
import com.fasterxml.jackson.databind.JsonNode;
import control.MainController;
import control.configuration.LayoutConfiguration;
import control.factories.LayoutConfigurationFactory;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import preprocessing.PreProcessor;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by FRudi on 17.12.2015.
 */
public class AnalyseWorker implements Runnable {

    private MainController controller;
    private Job job;
    private List<PreProcessor> preProcessors;
    private List<AnalyseType> analyser;
    private BufferedImage image;
    private LayoutConfiguration configuration;

    public AnalyseWorker(){
        this.controller = new MainController();
    }

    @Override
    public void run() {

        controller.analyse(image, configuration);
    }

    public void setImage(BufferedImage image){
        this.image = image;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setConfiguration(LayoutConfiguration configuration) {
        this.configuration = configuration;
    }
}

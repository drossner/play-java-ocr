package modules.database.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import modules.database.JobController;
import modules.database.entities.Image;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import modules.database.entities.User;
import org.joda.time.DateTime;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by florian on 01.12.15.
 */
public class SimpleJobFactory {

    private Job job = new Job();

    private Image image;

    private LayoutConfig layoutConfig;

    public Job persist(){
        if(image != null && layoutConfig != null && job.getUser() != null){
            new JobController().persistJob(job, image, layoutConfig, job.getUser());
        }else{
            Logger.error("no image set!");
        }

        return job;
    }

    public SimpleJobFactory setImage(Image image){
        this.image = image;
        return this;
    }

    public SimpleJobFactory setUser(User user){
        job.setUser(user);

        return this;
    }

    public SimpleJobFactory setName(String name){
        job.setName(name);

        return this;
    }

    public SimpleJobFactory setStartTime(DateTime startTime){
        job.setStartTime(startTime);

        return this;
    }

    public SimpleJobFactory setCategory(String category){
        job.setCategory(category);

        return this;
    }

    public SimpleJobFactory setLayoutParameters(LayoutConfig layoutConfig){

        this.layoutConfig = layoutConfig;

        return this;
    }

    public void createJobsJsonBulk(ObjectNode result, String session) throws IOException {
        JsonNode arrayNode = result.get("files");

        for(int i = 0; i < arrayNode.size(); i++){
            Logger.debug("create job: " + arrayNode.get(i));
            job = new Job();
            JsonNode fileNode = arrayNode.get(i);
            String path = fileNode.get("name").asText();

            //TODO DANIEL noch zu richtiger file machen (daniel is kacke, Zitat:14.12.2015 12:35 Benedikt Linke)
            File file = new File("./public/images/rechnungtest.png");
            BufferedImage img;

            img = ImageIO.read(file);
            Image image = new SimpleImageFactory()
                    .setCreateDate(new DateTime())
                    .setFocalLength(2.0)
                    .setSource(file.getAbsolutePath())
                    .build();

            Logger.info("setting path: " + file.getAbsolutePath());

            setName(path);
            setStartTime(new DateTime());

            new JobController().persistJob(job, image, session);
        }
    }
}

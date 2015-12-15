package modules.database.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import modules.cms.CMSController;
import modules.cms.FolderController;
import modules.cms.SessionHolder;
import modules.cms.data.FileType;
import modules.database.JobController;
import modules.database.entities.Image;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import modules.database.entities.User;
import modules.upload.FileContainer;
import modules.upload.ImageHelper;
import org.joda.time.DateTime;
import play.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public void createJobsJsonBulk(List<FileContainer> files, String session) throws IOException {
        CMSController cms = SessionHolder.getInstance().getController("ocr", "ocr");
        FolderController folderController = new FolderController(cms);

        for(int i = 0; i < files.size(); i++){
            Logger.debug("create job: " + files.get(i));
            job = new Job();

            String name = files.get(i).getFileName();

            String id = cms.createDocument(folderController.getUserWorkspaceFolder(), files.get(i).getFile(), FileType.FILE.getType()).getId();

            Image image = new SimpleImageFactory()
                    .setCreateDate(new DateTime())
                    .setFocalLength(2.0)
                    .setSource(id)
                    .build();

            setName(name);
            setStartTime(new DateTime());

            new JobController().persistJob(job, image, session);
        }
    }
}

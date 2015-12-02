package modules.database.factory;

import modules.database.JobController;
import modules.database.entities.Image;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import modules.database.entities.User;
import org.joda.time.DateTime;
import play.Logger;

/**
 * Created by florian on 01.12.15.
 */
public class SimpleJobFactory {

    private Job job = new Job();

    private Image image;

    public Job persist(){

        if(image != null){
            new JobController().persistJob(job, image);
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
        job.setLayoutConfig(layoutConfig);

        return this;
    }
}

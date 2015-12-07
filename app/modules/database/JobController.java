package modules.database;

import modules.database.entities.Image;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Created by florian on 02.12.15.
 */
public class JobController extends  DatabaseController<Image, User>{

    @Transactional
    public void persistJob(Job job, Image image, LayoutConfig layoutConfig) {
        JPA.em().persist(image);
        JPA.em().persist(layoutConfig);

        job.setImage(image);
        job.setLayoutConfig(layoutConfig);

        JPA.em().persist(job);
    }


}

package modules.database;

import modules.database.entities.Image;
import modules.database.entities.Job;
import modules.database.entities.LayoutConfig;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.util.List;

/**
 * Created by florian on 02.12.15.
 */
public class JobController extends  DatabaseController<Job, Object>{

    @Transactional
    public void persistJob(Job job, Image image, LayoutConfig layoutConfig, User user) {
        JPA.em().persist(image);
        JPA.em().persist(layoutConfig);

        job.setImage(image);
        job.setLayoutConfig(layoutConfig);
        job.setUser(selectEntity(User.class, "eMail", user.geteMail()));

        JPA.em().persist(job);
    }

    public List<Job> getJobsFromUser(String userName){
        User user = selectEntity(User.class, "eMail", userName);

        return selectEntityList(Job.class, user);
    }

    public Job getJobById(int id){
        return selectEntity(Job.class, "id", Integer.toString(id));
    }

}

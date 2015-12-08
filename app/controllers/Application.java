package controllers;


import be.objectify.deadbolt.core.models.Subject;
import com.google.inject.Inject;
import modules.database.entities.Country;
import modules.database.entities.User;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import modules.upload.UploadHandler;
import play.*;
//import play.api.mvc.*;
import play.cache.CacheApi;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.*;

import views.html.*;
import javax.persistence.TypedQuery;

public class Application extends Controller {

    private CacheApi cache;
    private UploadHandler uploadHandler;

    @Inject
    public Application(CacheApi cache, UploadHandler uploadHandler){
        this.cache = cache;
        this.uploadHandler = uploadHandler;
    }

    public Result index() {
        if(session().get("session") != null){
            return redirect(routes.Application.hochladen(2));
        } else {
            return ok(index.render());
        }
    }

    @SubjectPresent
    public Result secured(){
        return ok("this site is protected!");
    }

    @SubjectPresent
    public Result hochladen(int step){
        if(step == 1) {
            String uploadId = uploadHandler.createUploadId();
            //session().put(uploadId, ""+step);
            return ok(hochladen_1.render(uploadId));
        } else if (step == 2){
            return ok(hochladen_2.render());
        } else {
            return badRequest();
        }
    }

    @SubjectPresent
    public F.Promise<Result> verwalten(){
        final String userMail = session().get("session");
        return F.Promise.promise(() ->
                //auto open/close/commit transaction in this thread, readOnly = true
                JPA.withTransaction("default", true, () -> {
                    //load subject from cache of from database if not available
                    User user = cache.getOrElse(userMail, () -> {
                                TypedQuery<User> q = JPA.em().createQuery("select u from User u where u.eMail = :email", User.class);
                                q.setParameter("email", userMail);
                                return q.getSingleResult();
                            }
                    );
                    return ok(verwalten.render(user));
                })
        );
    }

    @SubjectPresent
    public Result ablage(){
        return ok(ablage.render());
    }

    @SubjectPresent
    public Result hilfe(){
        return ok(hilfe.render());
    }

    public Result template(){
        return ok(views.html.modals.templating.render());
    }

    public Result imgEdit(){
            return ok(views.html.modals.imageEdit.render());
        }

   /* @Transactional
    public Result testDatabase(){
        User temp = new DataCreator().getUser();
        JPA.em().persist(temp.getCountry());
        JPA.em().persist(temp);

        if(JPA.em().find(User.class, temp.getId()).geteMail().equals(temp.geteMail())){
            return ok(database.render("erfolgreich!"));
        }else{
            return ok(database.render("nicht erfolgreich!"));
        }
    }

    public class DataCreator{


        public User getUser(){
            User rc = new User();

            Country c = new Country();
            c.setName("Deutscheland");

            rc.seteMail("test@test.de");
            rc.setCountry(c);
            rc.setPassword("test");

            return rc;
        }

    } */
}

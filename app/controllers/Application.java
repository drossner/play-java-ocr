package controllers;


import be.objectify.deadbolt.core.PatternType;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.actions.Pattern;
import com.google.inject.Inject;
import controllers.security.OcrDeadboltHandler;
import controllers.security.OcrPermission;
import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.Job;
import modules.database.entities.User;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import modules.database.factory.SimpleJobFactory;
import modules.upload.UploadHandler;
import play.*;
//import play.api.mvc.*;
import play.cache.CacheApi;
import play.db.jpa.JPA;
import play.libs.F;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import javax.persistence.TypedQuery;
import java.util.List;

public class Application extends Controller {

    private CacheApi cache;
    private UploadHandler uploadHandler;

    @Inject
    public Application(CacheApi cache, UploadHandler uploadHandler) {
        this.cache = cache;
        this.uploadHandler = uploadHandler;
    }

    public Result index() {
        if (session().get("session") != null) {
            return redirect(routes.Application.hochladen(1, null));
        } else {
            String target = session().get("target");
            return ok(index.render(target != null, target));
        }
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public F.Promise<Result> hochladen(int step, String inUploadId) {
        final String userMail = session().get("session");
        return F.Promise.promise(() -> {
            if (step == 1) {
                String uploadId = uploadHandler.createUploadId();
                //session().put(uploadId, ""+step);
                return ok(hochladen_1.render(uploadId));
            } else if (step == 2) {
                //use inUploadId

                new SimpleJobFactory().createJobsJsonBulk(uploadHandler.loadFiles(inUploadId), userMail);

                return ok(hochladen_2.render());
            } else {
                return badRequest();
            }
        });
    }

    @SubjectPresent
    public F.Promise<Result> verwalten(boolean showWarning) {
        final String userMail = session().get("session");
        return F.Promise.promise(() ->
                //auto open/close/commit transaction in this thread, readOnly = true
                JPA.withTransaction("default", true, () -> {
                            User user = new modules.database.UserController().selectUserFromMail(userMail);

                            user.getCountry().setCountry(CountryImpl.ENGLISCH);
                            return ok(verwalten.render(user, showWarning));
                        }
                ));
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result ablage() {
        return ok(ablage.render());
    }

    @SubjectPresent
    public Result hilfe() {
        return ok(hilfe.render());
    }

    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result template() {
        return ok(views.html.modals.templating.render());
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

    private class UserTemp {
        public String language;
        public String email;

        public UserTemp(String email, String language) {
            this.email = email;
            this.language = language;
        }
    }
}

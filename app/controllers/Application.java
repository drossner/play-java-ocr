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

/**
 * Class controlling main requests for
 *  - index
 *  - hochladen
 *  - verwalten
 *  - ablage
 *  - hilfe
 *  - template => wird in hochladen 2 für das selektieren der bereiche verwendet
 */
public class Application extends Controller {

    private CacheApi cache;
    private UploadHandler uploadHandler;

    @Inject
    public Application(CacheApi cache, UploadHandler uploadHandler) {
        this.cache = cache;
        this.uploadHandler = uploadHandler;
    }

    /**
     * Gibt die gerenderte index.scala.html zurück, wenn man nicht eingeloggt ist, andernfalls die hochladen step 1 html Seite
     * @return rendert Page
     */
    public Result index() {
        if (session().get("session") != null) {
            return redirect(routes.Application.hochladen(1, null));
        } else {
            String target = session().get("target");
            return ok(index.render(target != null, target));
        }
    }

    /**
     * gibt entweder hochladen step 1 oder 2 zurück, dies ist abhängig vom übergebenen step.
     * upload id wird von step 1 an 2 übergeben und auf basis dieser id werden die in step 1 definierten dateien in die Datenbank gespeichert und daraufhin in der liste angezeigt
     * diese seite wird nur angezeigt, wenn der Benutzer einen CMS Account erstellt hat und dadurch die Rolle in der Datenbank erhalten hat
     * @param step gibt die seite an
     * @param inUploadId gibt die id an, welche gerade hochgeladen wurden
     * @return renderet page
     */
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

                if(inUploadId != null && uploadHandler.isUploadIdValid(inUploadId)){
                    List<Job> jobs = new modules.database.JobController().getJobsByUploadId(inUploadId);

                    if(jobs == null || jobs.size() == 0){
                        new SimpleJobFactory().createJobsJsonBulk(uploadHandler.loadFiles(inUploadId), inUploadId, userMail);
                    }
                    uploadHandler.invalidateUploadId(inUploadId);
                }

                return ok(hochladen_2.render(inUploadId));
            } else {
                return badRequest();
            }
        });
    }

    /**
     * gibt die werwalten.scala.html seite zurück
     * showWarning definiert die anzeige einer warnung, dass zunächst ein cms account erstellt werden soll
     * @param showWarning true => warnung wird angezeigt; false => warnung wird nicht angezeigt
     * @return gibt die gerenderte seite zurück
     */
    @SubjectPresent
    public F.Promise<Result> verwalten(boolean showWarning) {
        final String userMail = session().get("session");
        return F.Promise.promise(() ->
                //auto open/close/commit transaction in this thread, readOnly = true
                JPA.withTransaction("default", true, () -> {
                            User user = new modules.database.UserController().selectUserFromMail(userMail);

                            return ok(verwalten.render(user, showWarning));
                        }
                ));
    }

    /**
     * gibt die ablage.scala.html gerendert zurück
     * diese seite wird nur angezeigt, wenn der Benutzer einen CMS Account erstellt hat und dadurch die Rolle in der Datenbank erhalten hat
     * @return gerenderte seite
     */
    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result ablage() {
        return ok(ablage.render());
    }

    /**
     * gibt die hilfe.scala.html gerendert zurück
     * @return gerenderte seite
     */
    @SubjectPresent
    public Result hilfe() {
        return ok(hilfe.render());
    }

    /**
     * gibt template.scala.html zurück für das Iframe zur Konfiguration der untersuchenden Fragmente
     * diese seite wird nur angezeigt, wenn der Benutzer einen CMS Account erstellt hat und dadurch die Rolle in der Datenbank erhalten hat
     * @return
     */
    @Pattern(value="CMS", patternType = PatternType.EQUALITY, content = OcrDeadboltHandler.MISSING_CMS_PERMISSION)
    public Result template() {
        return ok(views.html.modals.templating.render());
    }
}

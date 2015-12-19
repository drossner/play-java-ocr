package controllers.security;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import controllers.Application;
import controllers.routes;
import errorhandling.ErrorHandler;
import modules.database.UserController;
import play.Logger;
import play.cache.CacheApi;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Created by Daniel on 21.11.2015.
 */
public class OcrDeadboltHandler extends AbstractDeadboltHandler {

    public final static String MISSING_CMS_PERMISSION = "MISSING_CMS_PERMISSION";

    private CacheApi cache;

    @Inject
    public OcrDeadboltHandler(CacheApi cache) {
        this.cache = cache;
    }

    public F.Promise<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.promise(() -> Optional.ofNullable(null/*redirect(routes.Application.index())*/));
    }

    public F.Promise<Optional<Subject>> getSubject(final Http.Context context) {
        // in a real application, the user name would probably be in the session following a login process
        final String userMail = context.session().get("session");
        if (userMail == null) {
            return F.Promise.promise(Optional::empty);
        } else {
            return F.Promise.promise(() -> Optional.ofNullable(
                    //auto open/close/commit transaction in this thread, readOnly = true
                    JPA.withTransaction("default", true, () -> {
                        //load subject from cache of from database if not available
                        /*Subject subj = cache.getOrElse(userMail, () -> {
                                    UserController controller = new UserController();
                                    return controller.selectUserFromMail(userMail);
                                }
                        );*/
                        Subject subj = new UserController().selectUserFromMail(userMail);
                        return subj;
                    })
            ));
        }

    }

    public F.Promise<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context) {
        //return F.Promise.promise(() -> Optional.of(new MyDynamicResourceHandler()));
        return F.Promise.promise(Optional::empty);
    }

    @Override
    public F.Promise<Result> onAuthFailure(final Http.Context context,
                                           final String content) {
        // you can return any result from here - forbidden, etc
        //return F.Promise.promise(() -> ok(accessFailed.render()));
        Logger.debug("DeadboltHandler content: " + content);
        if(content.equals(MISSING_CMS_PERMISSION)){
            return F.Promise.promise(() -> redirect(routes.Application.verwalten(true)));
        } else {
            String target = context.request().uri();
            context.session().put("target", target);
            return F.Promise.promise(() -> redirect(routes.Application.index()));
        }

    }
}

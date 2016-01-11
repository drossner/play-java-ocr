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
 * DeadboltHander used by this application. (Authentication Framework)
 * Methods in this class are called via the framework annotations.
 */
public class OcrDeadboltHandler extends AbstractDeadboltHandler {

    public final static String MISSING_CMS_PERMISSION = "MISSING_CMS_PERMISSION";

    private CacheApi cache;
    private UserController uc;

    @Inject
    public OcrDeadboltHandler(CacheApi cache) {
        this.cache = cache;
        this.uc = new UserController();
    }

    /**
     * Method performed before authentication check.
     * @param context
     * @return returning null means that everything is OK.  Return a real result if you want a redirect to a login page or somewhere else
     */
    public F.Promise<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        return F.Promise.promise(() -> Optional.ofNullable(null/*redirect(routes.Application.index())*/));
    }

    /**
     * Returns the current Subject (in this case: user)
     * @param context
     * @return Empty Promise if no Subject is present, otherwise the Subject
     */
    public F.Promise<Optional<Subject>> getSubject(final Http.Context context) {
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
                        Subject subj = uc.selectUserFromMail(userMail);
                        return subj;
                    })
            ));
        }

    }

    /**
     * Not used.
     * @param context
     * @return
     */
    public F.Promise<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context) {
        //return F.Promise.promise(() -> Optional.of(new MyDynamicResourceHandler()));
        return F.Promise.promise(Optional::empty);
    }

    /**
     * Method called after a failed authentication.
     * @param context
     * @param content optional String, specified by the framework annotations
     * @return
     */
    @Override
    public F.Promise<Result> onAuthFailure(final Http.Context context,
                                           final String content) {
        Logger.debug("DeadboltHandler content onAuth Failure: " + content);
        //try to check if this user is logged in
        String usermail = context.session().get("session");
        //if he is not, redirect him to regular login page
        if(usermail==null || usermail.equals("")){
            String target = context.request().uri();
            context.session().put("target", target);
            return F.Promise.promise(() -> redirect(routes.Application.index()));
        }
        //following cases have a present subject but missing permissions
        //redirect them to the page, on which they can get them
        else if(content.equals(MISSING_CMS_PERMISSION)){
            return F.Promise.promise(() -> redirect(routes.Application.verwalten(true)));
        } else {
            //this case should never happen if everything is okay
            Logger.error("Deadbolt recognized unknown content: "+content);
            Logger.error("User "+usermail+" got an internalServerError!");
            return F.Promise.promise(() -> internalServerError());
        }

    }
}

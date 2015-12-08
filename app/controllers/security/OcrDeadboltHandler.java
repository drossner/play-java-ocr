package controllers.security;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import controllers.routes;
import modules.database.UserController;
import org.hibernate.Query;
import org.hibernate.Session;
import play.cache.CacheApi;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.*;
import views.html.index;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Created by Daniel on 21.11.2015.
 */
public class OcrDeadboltHandler extends AbstractDeadboltHandler {

    private CacheApi cache;

    @Inject
    public OcrDeadboltHandler(CacheApi cache) {
        this.cache = cache;
    }

    public F.Promise<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.promise(() -> Optional.ofNullable(unauthorized("nicht eingeloggt")/*redirect(routes.Application.index())*/));
    }

    public F.Promise<Optional<Subject>> getSubject(final Http.Context context) {
        // in a real application, the user name would probably be in the session following a login process
        final String userMail = context.session().get("session");
        System.err.println("Cache null? " + (cache == null));
        if (userMail == null) {
            return F.Promise.promise(Optional::empty);
        } else {
            return F.Promise.promise(() -> Optional.ofNullable(
                    //auto open/close/commit transaction in this thread, readOnly = true
                    JPA.withTransaction("default", true, () -> {
                        //load subject from cache of from database if not available
                        Subject subj = cache.getOrElse(userMail, () -> {
                                    UserController controller = new UserController();
                                    modules.database.entities.User user = new modules.database.entities.User();
                                    user.seteMail(userMail);

                                    /*TypedQuery<Subject> q = JPA.em().createQuery("select u from User u where u.eMail = :email", Subject.class);
                                    q.setParameter("email", userMail);
                                    return q.getSingleResult();*/
                                    return controller.selectUserFromMail(user);
                                }
                        );
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
        return F.Promise.promise(() -> forbidden("forbidden"));
    }

    private class User implements Subject {
        private String identifier;

        public User(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public List<? extends Role> getRoles() {
            return Arrays.asList(new Role() {
                @Override
                public String getName() {
                    return "user";
                }
            });
        }

        @Override
        public List<? extends Permission> getPermissions() {
            return Arrays.asList(new Permission() {
                @Override
                public String getValue() {
                    return "user-perm";
                }
            });
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }
    }
}

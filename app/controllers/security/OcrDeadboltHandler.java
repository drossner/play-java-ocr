package controllers.security;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import controllers.routes;
import play.libs.F;
import play.mvc.*;
import views.html.index;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Created by Daniel on 21.11.2015.
 */
public class OcrDeadboltHandler extends AbstractDeadboltHandler {

    public F.Promise<Optional<Result>> beforeAuthCheck(final Http.Context context)
    {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.promise(() -> Optional.ofNullable(unauthorized("nicht eingeloggt")/*redirect(routes.Application.index())*/));
    }

    public F.Promise<Optional<Subject>> getSubject(final Http.Context context)
    {
        // in a real application, the user name would probably be in the session following a login process
        String userMail = context.session().get("session");
        if(userMail == null){
            return F.Promise.promise(Optional::empty);
        } else {
            return F.Promise.promise(() -> Optional.ofNullable(new User(userMail)));
        }

    }

    public F.Promise<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
    {
        //return F.Promise.promise(() -> Optional.of(new MyDynamicResourceHandler()));
        return F.Promise.promise(Optional::empty);
    }

    @Override
    public F.Promise<Result> onAuthFailure(final Http.Context context,
                                           final String content)
    {
        // you can return any result from here - forbidden, etc
        //return F.Promise.promise(() -> ok(accessFailed.render()));
        return F.Promise.promise(() -> forbidden("forbidden"));
    }

    private class User implements Subject{
        private String identifier;

        public User(String identifier){
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

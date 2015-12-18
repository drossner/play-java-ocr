package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import modules.database.UserController;
import modules.database.entities.User;
import modules.ldap.LdapController;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by Benedikt Linke on 17.12.15.
 */
public class VerwaltenController extends Controller{

    private final LdapController ldapController;

    @Inject
    public VerwaltenController(LdapController ldapController) {
        this.ldapController = ldapController;
    }

    @SubjectPresent
    public Result updateLdapAccount() throws Throwable {
        //from webclient
        String username = "fritzi";
        String password = "hubertus";

        UserController uc = new UserController();
        User user = JPA.withTransaction(() -> {
            return uc.selectUserFromMail(session().get("session"));
        });
        //exit method
        if(user.getCmsAccount() != null) return badRequest("You already set up a cms account!");

        if(ldapController.searchUser(username)){
            return ok("Nutzer existiert bereits");
        } else {
            user.setCmsAccount(username);
            user.setCmsPassword(password);
            ldapController.insert(user);
            JPA.withTransaction(() -> uc.persistUser(user));
        }

        return redirect(routes.Application.verwalten());
    }

}

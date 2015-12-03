package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrRole;
import modules.authentication.AuthResponse;
import modules.authentication.FacebookAuthentication;
import modules.authentication.OAuthentication;
import modules.database.factory.SimpleUserFactory;
import modules.database.entities.User;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;

/**
 * Created by Daniel on 19.11.2015.
 */
public class AuthenticationController extends Controller{
    private final OAuthentication gp;
    private final OAuthentication fb;

    @Inject
    public AuthenticationController(OAuthentication gp){
        this.gp = gp;
        this.fb = new FacebookAuthentication();
    }

    public Promise<Result> oauth(String error, String code, int method) {
        if(error != null || code == null){
            Logger.info(request().body().asText());
            return Promise.promise(() -> unauthorized());
        }

        return Promise.promise(() -> authorize(code, method));
    }

    public Result login(int method) {
        try {
            //testcode START
            if(method == 1){
                return redirect(fb.getAuthURL());
            }
            //testcode ENDE
            return redirect(gp.getAuthURL());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        }
    }

    @SubjectPresent
    public Result logout(){
        session().clear();
        return redirect(routes.Application.index());
    }

    private Result authorize(String code, int method){
        try {
            //testcode START
            if(method == 1){
                AuthResponse authResponse = fb.exchangeToken(code);
                return ok("Your Email is: " + authResponse.getEmail());
            }
            //testcode ENDE
            AuthResponse authResponse = gp.exchangeToken(code);
            if(authResponse.isValid()){
                return ok("Your Email is: " + authResponse.getEmail());
            } else {
                return badRequest("invalidToken");
            }
        } catch (IOException e) {
            Logger.error("oauth IO-Error", e);
            return internalServerError("error");
        }
    }


    public Promise<Result> stubLogin() {
        return Promise.promise(() -> JPA.withTransaction(() -> {
            session().clear();
            final String userEmail = "test@test.de";

            new SimpleUserFactory()
                    .setEmail(userEmail)
                    .setPassword("test")
                    .addRole(OcrRole.USER)
                    .persist();

            session("session", userEmail);
            return redirect(routes.Application.index());
        }));
    }

}

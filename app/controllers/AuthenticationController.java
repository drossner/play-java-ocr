package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrRole;
import modules.authentication.AuthResponse;
import modules.authentication.FacebookAuthentication;
import modules.authentication.OAuthentication;
import modules.database.entities.CountryImpl;
import modules.database.factory.SimpleUserFactory;
import modules.database.entities.User;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Daniel on 19.11.2015.
 * Authentication Controller handles the authentication within the oAuth2 process for facebook and google+
 */
public class AuthenticationController extends Controller{
    private final OAuthentication gp;
    private final OAuthentication fb;

    @Inject
    public AuthenticationController(OAuthentication gp){
        this.gp = gp;
        this.fb = new FacebookAuthentication();
    }

    /**
     * Called by the oAuth2-Server (facebook/ google)
     * @param error specific error code
     * @param code oAuth2 code, which can be parsed to get the access_token, username, etc.
     * @param method method identifier (0=google 1=facebook)
     * @return
     */
    public Promise<Result> oauth(String error, String code, int method) {
        if(error != null || code == null){
            Logger.info(request().body().asText());
            return Promise.promise(() -> unauthorized());
        }

        return Promise.promise(() -> authorize(code, method));
    }

    /**
     * Redirects the user to the oAuth2 URL (e.g. facebook/ google)
     * @param method
     * @return
     */
    public Result login(int method) {
        try {
            OAuthentication oauth = getOAuthenticationImpl(method);
            return redirect(oauth.getAuthURL());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        } catch (InvalidParameterException e){
            Logger.error(e.getMessage(), e);
            return badRequest("Invalid login method: " + method);
        }
    }

    /**
     * Invalidates the session.
     * @return
     */
    @SubjectPresent
    public Result logout(){
        session().clear();
        return redirect(routes.Application.index());
    }

    /**
     * Generates a AuthResponse containing userinformation from the oAuth content Provider
     * @param code oAuth2 response code
     * @param method method identifier (0=google 1=facebook)
     * @return
     */
    private Result authorize(String code, int method){
        try {
            OAuthentication oauth = getOAuthenticationImpl(method);
            AuthResponse authResponse = oauth.exchangeToken(code);
            if(authResponse.isValid()){
                return JPA.withTransaction(() -> setUpSession(authResponse));
            } else {
                return badRequest("invalidToken");
            }
        } catch (IOException e) {
            Logger.error("oauth IO-Error", e);
            return internalServerError("error");
        } catch (InvalidParameterException e){
            Logger.info(e.getMessage(), e);
            return badRequest("Invalid login method: " + method);
        } catch (Throwable e) {
            Logger.error("oauth IO-Error", e);
            return internalServerError("error");
        }
    }

    /**
     * Authorizes the user after successful authentication as logged in user.
     * @param authResponse
     * @return
     */
    private Result setUpSession(AuthResponse authResponse){
        //Is there a target present?
        String target = session().get("target");
        //clear the session()
        session().clear();
        //get the email of the user
        final String userEmail = authResponse.getEmail();
        User loadedUser = new SimpleUserFactory()
                .setEmail(userEmail)
                .setPassword("")
                //TODO @DANIEL wie bekommen wir das land?
                .addRole(OcrRole.USER)
                .persist();
        //session init
        session().put("session", userEmail);

        //ldap account?
        if(loadedUser.getCmsAccount() == null || loadedUser.getCmsAccount().equals("")){
            //redirect to verwalten to set up ldap
            return redirect(routes.Application.verwalten(true));
        }
        else if(target != null){
            return redirect(target);
        } else {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Get the OAuthentication Implementation
     * @param method method identifier (0=google 1=facebook)
     * @return OAuthentication
     * @throws InvalidParameterException
     */
    private OAuthentication getOAuthenticationImpl(int method) throws InvalidParameterException{
        if(method == 0)return gp;
        else if(method == 1) return fb;
        else throw new InvalidParameterException();
    }


    /**
     * Stublogin without facebook/ google or others (used for testing and not yes set up oAuth2 applications)
     * @return
     */
    public Promise<Result> stubLogin() {
        return Promise.promise(() -> JPA.withTransaction(() -> {
            String target = session().get("target");
            session().clear();
            final String userEmail = "test@testdffgdv.de";

            if(new SimpleUserFactory()
                    .setEmail(userEmail)
                    .setCountry(CountryImpl.GERMAN)
                    .setPassword("test")
                    .addRole(OcrRole.USER)
                    .persist() != null){
                session("session", userEmail);
            }

            if(target != null){
                return redirect(target);
            } else {
                return redirect(routes.Application.index());
            }
        }));
    }

}

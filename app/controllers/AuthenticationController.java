package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrRole;
import modules.authentication.AuthResponse;
import modules.authentication.FacebookAuthentication;
import modules.authentication.OAuthentication;
import modules.database.entities.CountryImpl;
import modules.database.factory.SimpleUserFactory;
import modules.database.entities.User;
import modules.ldap.LdapController;
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
import java.security.InvalidParameterException;

/**
 * Created by Daniel on 19.11.2015.
 */
public class AuthenticationController extends Controller{
    private final OAuthentication gp;
    private final OAuthentication fb;

    @Inject
    public AuthenticationController(OAuthentication gp, LdapController ldapController){
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

    @SubjectPresent
    public Result logout(){
        session().clear();
        return redirect(routes.Application.index());
    }

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

    private Result setUpSession(AuthResponse authResponse){
        //Is there a target present?
        String target = session().get("target");
        //clear the session()
        session().clear();
        //get the email of the user
        final String userEmail = authResponse.getEmail();
        //lookup if user aleady exists
        /*CriteriaBuilder qb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root user = cq.from(User.class);
        cq.select(qb.count(user));
        cq.where(qb.equal(user.get("eMail"), userEmail));
        boolean exists = JPA.em().createQuery(cq).getSingleResult() == 1;*/
        /*
        CriteriaBuilder qb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<User> cq = qb.createQuery(User.class);
        Root user = cq.from(User.class);
        cq.select(user);
        cq.where(qb.equal(user.get("eMail"), userEmail));
        User loadedUser = JPA.em().createQuery(cq).getSingleResult();
        boolean exists = loadedUser != null;
        //create new user if he doesnt exist
        if (!exists) {
            JPA.em().persist(new SimpleUserFactory()
                    .setEmail(userEmail)
                    .setPassword("")
                    .addRole(OcrRole.USER)
                    .persist());
        }
        */
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
            return redirect(routes.Application.verwalten());
        }
        else if(target != null){
            return redirect(target);
        } else {
            return redirect(routes.Application.index());
        }
    }

    private OAuthentication getOAuthenticationImpl(int method) throws InvalidParameterException{
        if(method == 0)return gp;
        else if(method == 1) return fb;
        else throw new InvalidParameterException();
    }


    public Promise<Result> stubLogin() {
        return Promise.promise(() -> JPA.withTransaction(() -> {
            String target = session().get("target");
            session().clear();
            final String userEmail = "test@test.de";

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

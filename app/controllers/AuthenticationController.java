package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import controllers.security.OcrRole;
import modules.authentication.AuthResponse;
import modules.authentication.FacebookAuthentication;
import modules.authentication.OAuthentication;
import modules.database.SimpleUserFactory;
import modules.database.entities.User;
import org.hibernate.Session;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
        //first of all clear the session()
        session().clear();
        //get the email of the user
        final String userEmail = authResponse.getEmail();
        //lookup if user aleady exists
        CriteriaBuilder qb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        Root user = cq.from(User.class);
        cq.select(qb.count(user));
        cq.where(qb.equal(user.get("eMail"), userEmail));
        boolean exists = JPA.em().createQuery(cq).getSingleResult() == 1;
        //create new user if he doesnt exist
        if (!exists) {
            JPA.em().persist(new SimpleUserFactory()
                    .setEmail(userEmail)
                    .setPassword("")
                    .addRole(OcrRole.USER)
                    .build());
        }
        //session init
        session().put("session", userEmail);
        return redirect(routes.Application.index());
    }

    private OAuthentication getOAuthenticationImpl(int method) throws InvalidParameterException{
        if(method == 0)return gp;
        else if(method == 1) return fb;
        else throw new InvalidParameterException();
    }


    public Promise<Result> stubLogin() {
        return Promise.promise(() -> JPA.withTransaction(() -> {
            session().clear();
            final String userEmail = "test@test.de";
            //lokup stub user userEmail
            //session creation
            //Session hibSession = JPA.em().unwrap(Session.class); // not recommendet: http://www.theserverside.com/news/2240186700/The-JPA-20-EntityManager-vs-the-Hibernate-Session-Which-one-to-use
            //hibSession.beginTransaction(); //done by transactional

            CriteriaBuilder qb = JPA.em().getCriteriaBuilder();
            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            Root user = cq.from(User.class);
            cq.select(qb.count(user));
            cq.where(qb.equal(user.get("eMail"), userEmail));
            boolean exists = JPA.em().createQuery(cq).getSingleResult() == 1;

            /*TypedQuery<Integer> q = JPA.em().createQuery("COUNT from User u where u.eMail = :email", Integer.class);
            q.setParameter("email", userEmail); */
            //boolean exists = q.getSingleResult() == 1;
            if (!exists) {
                JPA.em().persist(new SimpleUserFactory()
                        .setEmail(userEmail)
                        .setPassword("test")
                        .addRole(OcrRole.USER)
                        .build());
            }

            //hibSession.getTransaction().commit(); //done by transactional
            //hibSession.close(); //done by transactional
            session("session", userEmail);
            return redirect(routes.Application.index());
        }));
    }

}

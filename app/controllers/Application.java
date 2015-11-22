package controllers;


import modules.authentication.GoogleAuthentication;
import modules.database.entities.Country;
import modules.database.entities.User;
import play.*;
//import play.api.mvc.*;
import play.mvc.*;

import views.html.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

public class Application extends Controller {

    @PersistenceContext
    EntityManager em;

    public Result dummy() {
        return ok(dummy.render());
    }

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result oauth(String error, String code) {
        if(error != null || code == null) return unauthorized();

        try {
            return ok(GoogleAuthentication.getInstance().exchangeToken(code));
        } catch (IOException e) {
            Logger.error("oauth IO-Error", e);
            return internalServerError("error");
        }
        //return ok(code);
    }

    public Result oauthAccess(){
        return ok();
    }

    public Result login() {
        try {
            return redirect(GoogleAuthentication.getInstance().setUpGoogleClient());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        }
    }

    public Result testDatabase(){
        User temp = new DataCreator().getUser();
        em.persist(temp);

        if(em.find(User.class, temp.getId()).geteMail().equals(temp.geteMail())){
            return ok(database.render("erfolgreich!"));
        }else{
            return ok(database.render("nicht erfolgreich!"));
        }
    }

    public class DataCreator{


        public User getUser(){
            User rc = new User();

            Country c = new Country();
            c.setName("Deutscheland");

            rc.seteMail("test@test.de");
            rc.setCountry(c);
            rc.setPassword("test");

            return rc;
        }

    }
}

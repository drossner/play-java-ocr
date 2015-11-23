package controllers;


import modules.database.entities.Country;
import modules.database.entities.User;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
//import play.api.mvc.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;

import views.html.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class Application extends Controller {

 /*   @PersistenceContext
    EntityManager em; */

    @SubjectPresent
    public Result dummy() {
        return ok(dummy.render());
    }

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result upload() {
        return ok(upload.render("Your new application is ready."));
    }

    public Result buttonup() {
            return ok(buttonup.render("Your new application is ready."));
    }

    public Result secured(){
        return ok("this site is protected!");
    }

    @Transactional
    public Result testDatabase(){
        User temp = new DataCreator().getUser();
        JPA.em().persist(temp);

        if(JPA.em().find(User.class, temp.getId()).geteMail().equals(temp.geteMail())){
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

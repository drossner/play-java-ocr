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


    public Result index() {
        return ok(index.render());
    }

    public Result secured(){
        return ok("this site is protected!");
    }

    public Result hochladen(int step){
        if(step == 1) {
            return ok(hochladen_1.render());
        } else if (step == 2){
            return ok(hochladen_2.render());
        } else {
            return badRequest();
        }

    }

    public Result verwalten(){
        return ok(verwalten.render());
    }

    public Result ablage(){
        return ok(ablage.render());
    }

    public Result hilfe(){
        return ok(hilfe.render());
    }

   /* @Transactional
    public Result testDatabase(){
        User temp = new DataCreator().getUser();
        JPA.em().persist(temp.getCountry());
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

    } */
}

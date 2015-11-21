package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.*;
//import play.api.mvc.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

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

}

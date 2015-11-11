package controllers;

import modules.authentication.GoogleAuthentication;
import play.*;
//import play.api.mvc.*;
import play.mvc.*;

import views.html.*;

import java.io.IOException;

public class Application extends Controller {

    public Result dummy() {
        return ok(dummy.render());
    }

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result oauth(String code, String error) {
        if(error != null || code == null) return unauthorized();

        return ok(code);
    }

    public Result login() {
        try {
            return redirect(new GoogleAuthentication().setUpGoogleClient());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        }
    }
}

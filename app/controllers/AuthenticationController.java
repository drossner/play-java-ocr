package controllers;

import modules.authentication.AuthResponse;
import modules.authentication.GoogleAuthentication;
import modules.authentication.OAuthentication;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Daniel on 19.11.2015.
 */
public class AuthenticationController extends Controller{
    private final OAuthentication gp;

    @Inject
    public AuthenticationController(OAuthentication gp){
        this.gp = gp;
    }

    public Result oauth(String error, String code) {
        Logger.debug(request().body().asText());
        if(error != null || code == null){
            Logger.debug(request().body().asText());
            return unauthorized();
        }

        try {
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
        //return ok(code);
    }

    public Result login(int method) {
        try {
            return redirect(gp.getAuthURL());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        }
    }

}

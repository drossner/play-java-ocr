package controllers;

import modules.authentication.AuthResponse;
import modules.authentication.OAuthentication;
import play.Logger;
import play.libs.F.Promise;
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

    public Promise<Result> oauth(String error, String code) {
        if(error != null || code == null){
            Logger.info(request().body().asText());
            return Promise.promise(() -> unauthorized());
        }

        return Promise.promise(() -> authorize(code));
    }

    public Result login(int method) {
        try {
            return redirect(gp.getAuthURL());
        } catch (IOException e) {
            Logger.error("Login IO-Error", e);
            return internalServerError("error");
        }
    }

    private Result authorize(String code){
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
    }

}

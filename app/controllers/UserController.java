package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Created by FRudi on 17.12.2015.
 */
public class UserController extends Controller {

    public F.Promise<Result> getUser() {
        final String userMail = session().get("session");
        if (userMail == null) return F.Promise.promise(Results::unauthorized);
        return F.Promise.promise(() ->
                //auto open/close/commit transaction in this thread, readOnly = true
                JPA.withTransaction(() -> {
                    //load subject from cache of from database if not available
                    User user = new modules.database.UserController().selectUserFromMail(userMail);

                    return ok(Json.toJson(user));
                })
        );
    }

    public F.Promise<Result> saveUser() {
        final String userMail = session().get("session");
        return F.Promise.promise(() ->
                JPA.withTransaction(() -> {
                    //load subject from cache of from database if not available
                    modules.database.UserController controller = new modules.database.UserController();
                    User user = controller.selectUserFromMail(userMail);

                    JsonNode saveUser = request().body().asJson();
                    saveUser = saveUser.get("user");
                    Logger.info(saveUser.toString());

                    user.setCmsAccount(saveUser.get("cmsAccount").asText());

                    String password = saveUser.get("password").asText();
                    String passwordConfirm = saveUser.get("passwordConfirm").asText();

                    if((password == null || password.equals("")) || !password.equals(passwordConfirm)){
                        return badRequest("Passwörter stimmen nicht überein!");
                    }

                    user.setCmsPassword(password);
                    user.getCountry().setCountry(getCountryImpl(saveUser.get("language").get("language").asText()));

                    Logger.info(user.toString());

                    return ok("Änderungen übernommen!");
                })
        );
    }

    public CountryImpl getCountryImpl(String value){
        switch (value.toLowerCase()){
            case "deutsch":
                return CountryImpl.GERMAN;
            case "englisch":
                return CountryImpl.ENGLISCH;
            default:
                return CountryImpl.GERMAN;
        }
    }
}

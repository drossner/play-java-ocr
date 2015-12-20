package controllers;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.OcrPermission;
import modules.cms.CMSController;
import modules.cms.SessionHolder;
import modules.database.CountryController;
import modules.database.PermissionController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import modules.database.entities.UserPermission;
import modules.ldap.LdapController;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by FRudi und Daniel on 17.12.2015.
 */
@SubjectPresent
public class UserController extends Controller {

    private final String NUXEOLINK = "http://v22015042759824376.yourvserver.net:8080/";

    private final int PASSWORD_LENGTH = 5;
    private final int CMSACCOUNT_LENGTH = 5;

    private final String PASSWORD_CONSTRAINT_CHECK = "Die Passwörter stimmen nicht überein";
    private final String PASSWORD_CONSTRAINT_STRENGTH = "Das Passwort ist nicht lang genug";
    private final String CMS_CONSTRAINT_LENGTH = "CMS-Nutzername ist zu kurz";
    private final String CMS_CONSTRAINT_EXIST = "CMS-Nutzername existiert bereits";
    private final String CMS_CONSTRAINT_WANNABEUSER = "Sie haben bereits einen CMS-Nutzeraccount erstellt, dieser ist nicht editierbar";
    private final String LANGUAGE_NOT_IN_DATABASE = "Sprache ist leider nicht vorhanden";

    private final String SUCCESS_MESSAGE = "Änderungen wurden erfolgreich übernommen";


    private final LdapController ldapController;

    @Inject
    public UserController(LdapController ldapController) {
        this.ldapController = ldapController;
    }

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
                    //load user from database
                    modules.database.UserController controller = new modules.database.UserController();
                    User user = controller.selectUserFromMail(userMail);

                    //etract json from client
                    JsonNode sentUserData = request().body().asJson();
                    sentUserData = sentUserData.get("user");
                    Logger.info(sentUserData.toString());

                    //get values from Json
                    //TODO: empty values -> crash -> NullPointerException
                    String cmsAccount = sentUserData.get("cmsAccount").asText();
                    String password = sentUserData.get("password").asText();
                    String passwordConfirm = sentUserData.get("passwordConfirm").asText();
                    String language = sentUserData.get("language").asText();

                    ObjectNode result = null;
                    Country countryImpl = getCountry(language);

                    if((user.getCmsAccount() != null &&
                            !user.getCmsAccount().equals(cmsAccount)) ||
                            (user.getCmsAccount() != null &&
                            password.length() > 0)){
                        result = generateJsonResponse(false, CMS_CONSTRAINT_WANNABEUSER);
                    }else if(language == null || (countryImpl = getCountry(language)) == null) result = generateJsonResponse(false, LANGUAGE_NOT_IN_DATABASE);
                        //set all or only language -> if sth went wrong, dont set language!!!
                        //enter following if, if nothing changed -> only language
                    else if((user.getCmsAccount() == null || user.getCmsAccount().equals(cmsAccount)) &&
                            (password == null || password.length() == 0) &&
                            (passwordConfirm == null || passwordConfirm.length() == 0)){
                        user.setCountry(countryImpl);

                        return ok(generateJsonResponse(true, SUCCESS_MESSAGE));
                    }
                    else if(!password.equals(passwordConfirm)) result = generateJsonResponse(false, PASSWORD_CONSTRAINT_CHECK);
                    else if(password.length() <= PASSWORD_LENGTH){
                        result = generateJsonResponse(false, PASSWORD_CONSTRAINT_STRENGTH);
                    }
                    else{
                        //set the cmsAccount of the current user using the json data from client
                        result = setupLdapAccount(cmsAccount, password, user);
                    }

                    //set language if, and only if ldapAccpunt setup was successful
                    if(result.get("success").asBoolean()){
                        user.setCountry(countryImpl);
                    }
                    //print actual database user
                    Logger.info(user.toString());


                    if(result.get("success").asBoolean()){
                        return ok(result).as("application/json");
                    } else {
                        return badRequest(result).as("application/json");
                    }

                })
        );
    }

    public Country getCountry(String value){
        return new CountryController().selectEntity(Country.class, CountryImpl.getEnumInstance(value));
    }

    private ObjectNode generateJsonResponse(boolean success, String message){
        return generateJsonResponse(success, message, false);
    }

    private ObjectNode generateJsonResponse(boolean success, String message, boolean nuxeolink){
        ObjectNode result = Json.newObject();
        result.put("success", success);
        result.put("message", message);
        String link = nuxeolink? NUXEOLINK : null;
        result.put("nuxeolink", link);
        Logger.info(result.toString());
        return result;
    }


    private ObjectNode setupLdapAccount(String username, String password, User user) throws Throwable {
        ObjectNode result = null;
        //exit method
        if(username.length() <= CMSACCOUNT_LENGTH) result = generateJsonResponse(false, CMS_CONSTRAINT_LENGTH);

        else if(ldapController.searchUser(username)){
            result = generateJsonResponse(false, CMS_CONSTRAINT_EXIST);
        } else {
            user.setCmsAccount(username);
            user.setCmsPassword(password);
            PermissionController pc = new PermissionController();
            //UserPermission up = pc.getPermission(OcrPermission.CMS);
            user.getPermissions().add(pc.selectEntity(UserPermission.class, OcrPermission.CMS));
            ldapController.insert(user);
            CMSController cmsc = SessionHolder.getInstance().getController(username, password);
            result = generateJsonResponse(true, SUCCESS_MESSAGE, true);
        }

        return result;
    }
}

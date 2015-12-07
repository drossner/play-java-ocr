package modules.database;

import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.CountryImpl;
import play.Logger;
import play.db.jpa.Transactional;

import javax.inject.Singleton;

/**
 * Created by florian on 02.12.15.
 */
//@Singleton
public class InitDatabase extends DatabaseController {

    private static InitDatabase INSTANCE;

    public static InitDatabase getInstance(){
        if(INSTANCE == null){
            INSTANCE = new InitDatabase();
        }

        return INSTANCE;
    }

    private InitDatabase(){
        createCountries();

        createSecurityRoles();

        createUserPermissions();
    }

    private void createCountries(){
        Logger.info("creating countries");
        Object[] possibleValues = CountryImpl.GERMAN.getDeclaringClass().getEnumConstants();

        if(possibleValues != null){
            for (Object obj :
                    possibleValues) {
                persistObject(obj);
            }
        }

        Logger.info("countries created");
    }

    private void createSecurityRoles(){
        Logger.info("creating security roles");
        Object[] possibleValues = OcrRole.USER.getDeclaringClass().getEnumConstants();

        if(possibleValues != null){
            for (Object obj :
                    possibleValues) {
                persistObject(obj);
            }
        }

        Logger.info("security roles created");
    }

    private void createUserPermissions(){
        Logger.info("creating user permissions");
        Object[] possibleValues = OcrPermission.FULL.getDeclaringClass().getEnumConstants();

        if(possibleValues != null){
            for (Object obj :
                    possibleValues) {
                persistObject(obj);
            }
        }

        Logger.info("user permissions created");
    }
}

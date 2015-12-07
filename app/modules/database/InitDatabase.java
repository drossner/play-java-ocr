package modules.database;

import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.SecurityRole;
import modules.database.entities.UserPermission;
import play.Application;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by florian on 02.12.15.
 */
public class InitDatabase extends DatabaseController {

    /*private static InitDatabase INSTANCE;

    public static InitDatabase getInstance(){
        if(INSTANCE == null){
            INSTANCE = new InitDatabase();
        }

        return INSTANCE;
    }*/

    public InitDatabase(){
        Logger.info("Contructor Init Database");
        JPA.withTransaction(() -> {
            createCountries();

            createSecurityRoles();

            createUserPermissions();
        });
    }

    private void createCountries(){
        Logger.info("creating countries");
        Object[] possibleValues = CountryImpl.GERMAN.getDeclaringClass().getEnumConstants();

        if(possibleValues != null){
            for (Object obj :
                    possibleValues) {
                Country temp = new Country();
                temp.setCountry((CountryImpl) obj);

                if(selectEntity(Country.class, (CountryImpl) obj) == null){
                    persistObject(temp);
                }
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
                SecurityRole role = new SecurityRole((OcrRole) obj);
                if(selectEntity(SecurityRole.class, (OcrRole) obj) == null){
                    persistObject(role);
                }
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
                UserPermission permission = new UserPermission();
                permission.setValue((OcrPermission) obj);

                if(selectEntity(UserPermission.class, (OcrPermission) obj) == null){
                    persistObject(permission);
                }
            }
        }

        Logger.info("user permissions created");
    }
}

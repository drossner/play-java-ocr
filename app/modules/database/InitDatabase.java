package modules.database;

import control.result.Type;
import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.*;
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

    /**
     * initialisiert die datenbank
     */
    public InitDatabase(){
        Logger.info("Contructor Init Database");
        JPA.withTransaction(() -> {
            createCountries();

            createSecurityRoles();

            createUserPermissions();

            createLayoutConfig();
        });
    }

    /**
     * erstellt die für jeden benutzer vorhandenen layoutconfigurationen/templates
     */
    private void createLayoutConfig() {
        String name = "Brief";

        if(new LayoutConfigurationController().selectEntityList(LayoutConfig.class, "name", name) != null){
            return;
        }

        LayoutConfig config = new LayoutConfig();

        config.setLanguage(new CountryController().selectEntity(Country.class, CountryImpl.GERMAN));
        config.setName(name);
        config.setUser(null);

        LayoutFragment fragment1 = new LayoutFragment();
        fragment1.setLayoutConfig(config);
        fragment1.setxEnd(0.927140255009107);
        fragment1.setxStart(0.65391621129326);
        fragment1.setyEnd(0.108665749656121);
        fragment1.setyStart(0.0357634112792297);
        fragment1.setType(Type.IMAGE.name());

        LayoutFragment fragment2 = new LayoutFragment();
        fragment2.setLayoutConfig(config);
        fragment2.setxEnd(0.48816029143898);
        fragment2.setxStart(0.0728597449908925);
        fragment2.setyEnd(0.321870701513067);
        fragment2.setyStart(0.154057771664374);
        fragment2.setType(Type.TEXT.name());

        LayoutFragment fragment3 = new LayoutFragment();
        fragment3.setLayoutConfig(config);
        fragment3.setxEnd(0.73224043715847);
        fragment3.setxStart(0.0655737704918033);
        fragment3.setyEnd(0.573590096286107);
        fragment3.setyStart(0.360385144429161);
        fragment3.setType(Type.TEXT.name());

        LayoutFragment fragment4 = new LayoutFragment();
        fragment4.setLayoutConfig(config);
        fragment4.setxEnd(0.366120218579235);
        fragment4.setxStart(0.0710382513661202);
        fragment4.setyEnd(0.711141678129299);
        fragment4.setyStart(0.620357634112792);
        fragment4.setType(Type.IMAGE.name());

        JPA.em().persist(config);

        JPA.em().persist(fragment1);
        JPA.em().persist(fragment2);
        JPA.em().persist(fragment3);
        JPA.em().persist(fragment4);

    }

    /**
     * erstellt die für den benutzer verfügbaren sprachen in der datenbank
     * dabei werden alle enumeration constanten der enum countryimpl abgespeichert
     */
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

    /**
     * erstellt die für das system vorhandenen sicherheits relevaten rollen
     * dabei werden alle enumeration constanten der enum ocrrole abgespeichert
     */
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

    /**
     * erstellt die für das system vorhandenen sicherheits relevaten zugriffsrechte
     * dabei werden alle enumeration constanten der enum ocrpermission abgespeichert
     */
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

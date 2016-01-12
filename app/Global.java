import modules.database.InitDatabase;
import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 *
 * Created by florian on 07.12.15.
 */
public class Global extends GlobalSettings {

    /**
     * wird bei server start zu beginn aufgerufen
     * initialisiert die datenbank mit dem aufruf der initdatabase klasse
     * @param app
     */
    public void onStart(Application app){
        Logger.info("Application before Init Database");

        new InitDatabase();

        Logger.info("Application started");
    }
}

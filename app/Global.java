import modules.database.InitDatabase;
import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by florian on 07.12.15.
 */
public class Global extends GlobalSettings {

    public void onStart(Application app){
        Logger.info("Application before Init Database");

        new InitDatabase();

        Logger.info("Application started");
    }
}

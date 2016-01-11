package modules.cms;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;


/**
 * Created by Benedikt Linke on 27.11.15.
 */
public class SessionHolder {

    //Setzen des CMIS Endpunktes
    private static final String CMIS_ENDPOINT = "http://v22015042759824376.yourvserver.net:8080/nuxeo/atom/cmis";
    //Definieren der Sesison Zeit
    private static final long MAX_SESSION_TIME = 1000*60*30;

    // Map, welche alle aktiven Session enthält
    Map<String, CMSSession> sessions = new TreeMap<>();

    private static SessionHolder instance = null;

    /**
     * Konstruktor
     * Ruft die Methode zur Überprüfung einer Session; alle 30 Minuten auf
     */
    private SessionHolder() {
        Akka.system().scheduler().schedule(
                Duration.create(30, TimeUnit.MINUTES),
                Duration.create(30, TimeUnit.MINUTES),   // Aufruf des Jobs alle 30 Minuten
                (Runnable) this::checkSessions, Akka.system().dispatcher());
    }

    // Ein Singelton Objekt
    public static SessionHolder getInstance() {
        if(instance == null) {
            instance = new SessionHolder();
        }
        return instance;
    }

    /**
     * Läd eine Session aus einer SessionMap.
     * Falls keine Session existiert wird eine Neue erzeugt
     * @param username  wird zur Authentifizieren benötigt
     * @param password wird zur Authentifizieren benötigt
     * @return CMSController
     */
    public CMSController getController(String username, String password){
        if(sessions.containsKey(username)){
            CMSSession sessionCMS = sessions.get(username);

            sessionCMS.setLastActivity(new Date());

            Logger.info("get session: " + username);
            return new CMSController(sessionCMS);
        }else{
            sessions.put(username, createSession(username, password));
            sessions.get(username).setLastActivity(new Date());

            Logger.info("create session: " + username);
            return new CMSController(sessions.get(username));
        }
    }



    /**
     * Erstellen einer Session und verbindet diese mit dem Repository
     * @param username wird zur Authentifizieren benötigt
     * @param password wird zur Authentifizieren benötigt
     * @return eine neue CMSSession
     */
    private CMSSession createSession(String username, String password) {
        // default factory implementation
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // Nutzer-Anmeldedaten setzen
        parameter.put(SessionParameter.USER, username);
        parameter.put(SessionParameter.PASSWORD, password);

        // Verbindungseinstellungen stezen
        parameter.put(SessionParameter.ATOMPUB_URL, CMIS_ENDPOINT);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // Nimmt das erste Repository
        List<Repository> repositories = sessionFactory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();

        // Schaltet den Sessioncache aus
        session.getDefaultContext().setCacheEnabled(false);

        return new CMSSession(username, session);
    }


    /**
     * Überprüft, ob eine Session noch valide ist.
     * Wenn nicht wird diese nach einer definierten Zeit gelöscht.
     */
    public void checkSessions() {
        for(String username: sessions.keySet()){
            if(new Date().getTime() - sessions.get(username).getLastActivity().getTime() > MAX_SESSION_TIME ){
                sessions.remove(username);
            }
        }
    }

}

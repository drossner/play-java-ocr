package modules.cms;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;


/**
 * Created by Benedikt Linke on 27.11.15.
 */
public class SessionHolder {

    Map<String, SessionCMS> sessions = new HashMap<String, SessionCMS>();

    private static SessionHolder instance = null;
    private SessionHolder() {}

    public static SessionHolder getInstance() {
        if(instance == null) {
            instance = new SessionHolder();
        }
        return instance;
    }

    public void storeSession(String sessionName, SessionCMS session) {
        sessions.put(sessionName,session);
    }

    public SessionCMS retrieveSession(String sessionName) {
        return sessions.get(sessionName);
    }

    void disconnectSession(String sessionName) {
        sessions.remove(sessionName);
    }
}

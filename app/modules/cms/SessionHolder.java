package modules.cms;

import java.util.*;
import java.util.concurrent.RunnableFuture;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import play.Logger;


/**
 * Created by Benedikt Linke on 27.11.15.
 */
public class SessionHolder {

    private static final String CMIS_ENDPOINT = "http://v22015042759824376.yourvserver.net:8080/nuxeo/atom/cmis";
    private static final long MAX_SESSION_TIME = 0;

    Map<String, SessionCMS> sessions = new TreeMap<>();

    private static SessionHolder instance = null;

    private SessionHolder() {
        new Thread(new ActivityChecker()).start();
    }

    public static SessionHolder getInstance() {
        if(instance == null) {
            instance = new SessionHolder();

        }
        return instance;
    }

    public CmsController getController(String username, String password){
        if(sessions.containsKey(username)){
            SessionCMS sessionCMS = sessions.get(username);

            sessionCMS.setLastActivity(new Date());

            Logger.info("get session: " + username);
            return new CmsController(sessionCMS);
        }else{
            sessions.put(username, createSession(username, password));
            sessions.get(username).setLastActivity(new Date());

            Logger.info("create session: " + username);
            return new CmsController(sessions.get(username));
        }
    }

    private SessionCMS createSession(String username, String password) {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, username);
        parameter.put(SessionParameter.PASSWORD, password);
        parameter.put(SessionParameter.ATOMPUB_URL, CMIS_ENDPOINT);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // Use the first repository
        List<Repository> repositories = sessionFactory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();

        // Turn off the session cache completely
        session.getDefaultContext().setCacheEnabled(false);

        return new SessionCMS(username, session);
    }

    void disconnectSession(String sessionName) {
        sessions.remove(sessionName);
    }


    public void checkSessions() {
        for(String username: sessions.keySet()){
            if(new Date().getTime() - sessions.get(username).getLastActivity().getTime() > MAX_SESSION_TIME ){
                sessions.remove(username);
            }
        }
    }

}

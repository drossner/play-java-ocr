package modules.cms;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Benedikt Linke on 23.11.15.
 */
public class SessionCMS {

    private static final String CMIS_ENDPOINT = "http://v22015042759824376.yourvserver.net:8080/nuxeo/atom/cmis";

    private String username;

    private String password;

    private Session session;


    public SessionCMS(String username, String password){
        this.username = username;
        this.password = password;

        // create a session
        this.session = createSession();
    }

    protected Session createSession() {
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

        return session;
    }

    public Session getSession(){
        return  session;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

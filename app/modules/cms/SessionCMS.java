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


    private String username;

    private String password;

    private String baseUrl;

    private Session session;


    public SessionCMS(String username, String password, String baseUrl){
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
        // create a session
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, username);
        parameter.put(SessionParameter.PASSWORD, password);
        parameter.put(SessionParameter.ATOMPUB_URL, baseUrl);
        parameter.put(SessionParameter.BINDING_TYPE,
                BindingType.ATOMPUB.value());
        // Use the first repository
        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();
        session.getRootFolder();
        this.session = session;
        // Turn off the session cache completely
        session.getDefaultContext().setCacheEnabled(false);
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

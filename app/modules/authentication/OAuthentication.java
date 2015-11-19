package modules.authentication;

import java.io.IOException;

/**
 * Created by Daniel on 19.11.2015.
 */
public interface OAuthentication {

    String getAuthURL() throws IOException;

    AuthResponse exchangeToken(String code) throws IOException;

}

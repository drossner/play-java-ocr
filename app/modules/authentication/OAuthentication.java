package modules.authentication;

import com.google.inject.ImplementedBy;

import java.io.IOException;

/**
 * Created by Daniel on 19.11.2015.
 */
@ImplementedBy(GoogleAuthentication.class)
public interface OAuthentication {

    String getAuthURL() throws IOException;

    AuthResponse exchangeToken(String code) throws IOException;

}

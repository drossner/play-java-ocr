package modules.authentication;

import com.google.inject.ImplementedBy;

import java.io.IOException;

/**
 * Created by Daniel on 19.11.2015.
 * Interface for an oAuth2 authorization flow
 */
@ImplementedBy(GoogleAuthentication.class)
public interface OAuthentication {

    /**
     * Generates the URL for user authorization. oAuth2 provider specific
     * @return
     * @throws IOException
     */
    String getAuthURL() throws IOException;

    /**
     * Exchanges the oAuth2 code with an access-token, which is used to query the user information.
     * @param code
     * @return Encapsulated user information
     * @throws IOException
     */
    AuthResponse exchangeToken(String code) throws IOException;

}

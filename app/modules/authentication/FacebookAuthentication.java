package modules.authentication;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

/**
 * Created by Daniel on 20.11.2015.
 */
public class FacebookAuthentication implements  OAuthentication {
    private OAuthService oAuthService;

    public FacebookAuthentication(){
        oAuthService = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey("1541577662799415")
                .apiSecret("5cbc1c463eec2f77a2fb6e512f04f453")
                .callback("127.0.0.1")
                .build();
    }


    @Override
    public String getAuthURL() throws IOException {
        return oAuthService.getAuthorizationUrl(oAuthService.getRequestToken());
    }

    @Override
    public AuthResponse exchangeToken(String code) throws IOException {
        Verifier verfier = new Verifier(code);
        oAuthService.getAccessToken(null, verfier);
        return null;
    }
}

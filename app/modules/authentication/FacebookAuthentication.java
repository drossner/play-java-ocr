package modules.authentication;


import com.fasterxml.jackson.databind.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import play.libs.Json;

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
                //.callback("http://v22015042759824376.yourvserver.net:9000/oauth2callback")
                .callback("http://v22015042759824376.yourvserver.net:9000/oauth2callback_fb")
                .scope("email")
                .build();
    }


    @Override
    public String getAuthURL() throws IOException {
        return oAuthService.getAuthorizationUrl(null);
    }

    @Override
    public AuthResponse exchangeToken(String code) throws IOException {
        final Verifier verifier = new Verifier(code);
        final Token accessToken = oAuthService.getAccessToken(null, verifier);
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/v2.2/me?fields=email,name");
        oAuthService.signRequest(accessToken, request);
        final Response response = request.send();
        //parse response body
        JsonNode jsonNode = Json.parse(response.getBody());
        String email = jsonNode.get("email") == null ? null : jsonNode.get("email").textValue();
        String name = jsonNode.get("name") == null ? null : jsonNode.get("name").textValue();
        return new AuthResponse((email != null), email, name, null);
    }
}

package modules.authentication;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.plus.Plus;
import constants.AppValues;
import play.Logger;
import play.Play;


import javax.inject.Singleton;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * Created by Daniel on 11.11.2015.
 * Google+ Implementation
 */
@Singleton
public class GoogleAuthentication implements OAuthentication{

    //private static GoogleAuthentication instance;
    private GoogleClientSecrets gcs;
    private GoogleAuthorizationCodeFlow gacf;
    private String redirectURI;

    public GoogleAuthentication() throws IOException {
        gcs = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(Play.application().classloader().getResourceAsStream("client_secret.json")));

        redirectURI = gcs.getDetails().getRedirectUris().get(0);

        gacf = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                gcs.getDetails().getClientId(),
                gcs.getDetails().getClientSecret(),
                Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"))
                .setApprovalPrompt("force").build();
    }

    /**public static GoogleAuthentication getInstance() throws IOException {
        if(instance == null) instance = new GoogleAuthentication();
        return instance;
    }*/

    /**
     * @inheritDoc
     */
    public String getAuthURL() throws IOException {
        return gacf.newAuthorizationUrl().setRedirectUri(redirectURI).build();
    }

    /**
     * @inheritDoc
     */
    public AuthResponse exchangeToken(String token) throws IOException {
        GoogleTokenResponse gtr = gacf.newTokenRequest(token).setRedirectUri(gcs.getDetails().getRedirectUris().get(0)).execute(); //get(1)
        GoogleIdToken idToken = gtr.parseIdToken();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance()).setAudience(
                Arrays.asList(gcs.getDetails().getClientId())
        ).build();

        //create response object
        AuthResponse authResponse;
        try {
            if(verifier.verify(idToken)){
                //build response with all necessary data
                String email = idToken.getPayload().getEmail();
                authResponse = new AuthResponse(true, email, null, null);
            } else {
                authResponse = new AuthResponse(false, null, null, null);
            }
        } catch (GeneralSecurityException e) {
            Logger.info("GoogleIdToken verification failed", e);
            authResponse = new AuthResponse(false, null, null, null);
        }

        return authResponse;
    }

}

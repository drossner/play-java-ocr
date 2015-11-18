package modules.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import constants.AppValues;
import play.Play;


import java.io.*;
import java.util.Arrays;

/**
 * Created by Daniel on 11.11.2015.
 */
public class GoogleAuthentication {

    private static GoogleAuthentication instance;
    private GoogleClientSecrets gcs;
    private GoogleAuthorizationCodeFlow gacf;
    private String redirectURI;

    private GoogleAuthentication() throws IOException {
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

    public static GoogleAuthentication getInstance() throws IOException {
        if(instance == null) return new GoogleAuthentication();
        else return instance;
    }

    public String setUpGoogleClient() throws IOException {
        return gacf.newAuthorizationUrl().setRedirectUri(redirectURI).build();
    }

    public String exchangeToken(String token) throws IOException {
        String accessToken = gacf.newTokenRequest(token).setRedirectUri(gcs.getDetails().getRedirectUris().get(0)).execute().getIdToken(); //get(1)
        //gacf.newTokenRequest(token).
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return credential.getServiceAccountId();
    }

}

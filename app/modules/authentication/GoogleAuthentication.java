package modules.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson2.JacksonFactory;
import play.Play;


import java.io.*;
import java.util.Arrays;

/**
 * Created by Daniel on 11.11.2015.
 */
public class GoogleAuthentication {

    public String setUpGoogleClient() throws IOException {
        GoogleClientSecrets gcs = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new InputStreamReader(Play.application().classloader().getResourceAsStream("client_secret.json")));

        return gcs.getDetails().getAuthUri();
    }
}

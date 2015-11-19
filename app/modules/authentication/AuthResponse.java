package modules.authentication;

/**
 * Created by Daniel on 19.11.2015.
 */
public class AuthResponse {

    private final boolean valid;
    private final String email;
    private final String name1;
    private final String name2;

    public AuthResponse(boolean valid, String email, String name1, String name2) {
        this.valid = valid;
        this.email = email;
        this.name1 = name1;
        this.name2 = name2;
    }

    public boolean isValid() {
        return valid;
    }

    public String getEmail() {
        return email;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }
}

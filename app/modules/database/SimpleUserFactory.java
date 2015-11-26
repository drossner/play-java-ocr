package modules.database;

import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.User;

/**
 * Created by Daniel on 25.11.2015.
 */
public class SimpleUserFactory{

    private User user = new User();

    public User build(){
        user.setCountry(new Country("Deutschland", 276));
        return user;
    }

    public SimpleUserFactory setEmail(String email){
        user.seteMail(email);
        return this;
    }

    public SimpleUserFactory setPassword(String pw){
        user.setPassword(pw);
        return this;
    }

    public SimpleUserFactory addRole(OcrRole role){
        user.addRole(role);
        return this;
    }
}
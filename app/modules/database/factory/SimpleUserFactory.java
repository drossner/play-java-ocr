package modules.database.factory;

import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Created by Daniel on 25.11.2015.
 */
public class SimpleUserFactory{

    private User user = new User();

    public User build(){
        Country c = new Country("Deutschland", 276);

        JPA.em().persist(c);

        user.setCountry(c);
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

    @Transactional
    public SimpleUserFactory addRole(OcrRole role){
        user.addRole(role);
        return this;
    }
}

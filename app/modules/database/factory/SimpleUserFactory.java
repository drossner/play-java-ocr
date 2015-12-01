package modules.database.factory;

import controllers.security.OcrRole;
import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Created by Daniel on 25.11.2015.
 */
public class SimpleUserFactory{

    private User user = new User();

    public User persist(){
        UserController controller = new UserController();



        return user;
    }

    public SimpleUserFactory setCountryIso(int countryIso){
        Country country = new Country();
        country.setIsoCode(countryIso);

        user.setCountry(country);
        return this;
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

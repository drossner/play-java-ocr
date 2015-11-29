package modules.database;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.User;
import org.hibernate.criterion.Projection;

import java.util.List;

/**
 * Created by FRudi on 26.11.2015.
 */
public class UserController extends DatabaseController<User, Country> {

    public void persistUser(User user, int countryISO, List<Role> roles, List<Permission> permissions) throws Exception {
        Country country = selectEntity(Country.class, null);

        if(country != null){
            user.setCountry(country);
        }else{
            throw new Exception("false country iso");
        }



        persistObject(user);
    }

    public void persist(User user, int countryISO) throws Exception {
        Country country = selectEntity(Country.class, null);

        if(country != null){
            user.setCountry(country);
        }else{
            throw new Exception("false country iso");
        }

        persistObject(user);
    }
}

package modules.database.factory;

import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.UserController;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.management.relation.Role;
import java.util.ArrayList;

/**
 * Created by Daniel on 25.11.2015.
 */
public class SimpleUserFactory{

    private User user = new User();
    private ArrayList<OcrRole> roleList = new ArrayList<>();
    private ArrayList<OcrPermission> permissionList = new ArrayList<>();

    public User persist(){
        UserController controller = new UserController();

        if(controller.selectUserFromMail(user) == null){
            if(user.getCountry() == null){
                Country country = new Country();
                country.setCountry(CountryImpl.ENGLISCH);
                user.setCountry(country);
            }
            controller.persistUser(user, roleList, permissionList);
        }

        return controller.selectUserFromMail(user);
    }

    public SimpleUserFactory setCountry(CountryImpl c){
        Country country = new Country();
        country.setCountry(c);

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

    public SimpleUserFactory addRole(OcrRole role){
        roleList.add(role);
        return this;
    }

    public SimpleUserFactory addPermission(OcrPermission permission){
        permissionList.add(permission);
        return this;
    }
}

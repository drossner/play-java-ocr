package modules.database;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import org.hibernate.criterion.Projection;
import play.db.jpa.Transactional;

import java.util.List;

/**
 * Created by FRudi on 26.11.2015.
 */
public class UserController extends DatabaseController<User, CountryImpl> {

    private final RolesController rolesController;
    private final PermissionController permissionController;

    public UserController () {
        rolesController = new RolesController();
        permissionController = new PermissionController();
    }

    public void persistUser(User user, List<OcrRole> roles, List<OcrPermission> permissions){
        Country country = selectEntity(Country.class, user.getCountry().getCountry());
        user.setCountry(country);
        user.setRoles(rolesController.getRoles(roles));
        user.setPermission(permissionController.getPermissions(permissions));

        persistObject(user);
    }

    public void persistUser(User user) {
        persistObject(user);
    }


    public User selectUserFromMail(User user){
        return new UserEmailSelectController().selectUserFromMail(user);
    }

    private class UserEmailSelectController extends DatabaseController<User, String>{

        public User selectUserFromMail(User user){
            return selectEntity(User.class, "eMail", user.geteMail());
        }
    }
}
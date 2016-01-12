package modules.database;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.CountryImpl;
import modules.database.entities.User;
import org.hibernate.criterion.Projection;
import play.Logger;
import play.db.jpa.JPA;
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

    /**
     * selektiert die übergebenen rollen und rechte aus der datenbank, übergibt diese dem user und speichert den user zuletzt ab
     * @param user zu speichernder user
     * @param roles rollen des benutzers
     * @param permissions rechte des benutzer
     */
    public void persistUser(User user, List<OcrRole> roles, List<OcrPermission> permissions){
        Logger.info("persist user: " + user);
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
        return selectEntity(User.class, "eMail", user.geteMail());
    }

    public User selectUserFromMail(String userMail){
        return selectEntity(User.class, "eMail", userMail);
    }
}
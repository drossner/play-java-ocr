package modules.database;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrPermission;
import controllers.security.OcrRole;
import modules.database.entities.Country;
import modules.database.entities.User;
import org.hibernate.criterion.Projection;
import play.db.jpa.Transactional;

import java.util.List;

/**
 * Created by FRudi on 26.11.2015.
 */
public class UserController extends DatabaseController<User, Country> {

    private final RolesController rolesController;
    private final PermissionController permissionController;

    public UserController () {
        rolesController = new RolesController();
        permissionController = new PermissionController();
    }

    @Transactional
    public void persistUser(User user, List<OcrRole> roles, List<OcrPermission> permissions){
        user.setRoles(rolesController.getRoles(roles));
        user.setPermission(permissionController.getPermissions(permissions));

        persistObject(user);
    }

    @Transactional
    public void persistUser(User user) {
        persistObject(user);
    }
}
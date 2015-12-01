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

    private final RolesController rolesController;
    private final PermissionController permissionController;

    private UserController () {
        rolesController = new RolesController();
        permissionController = new PermissionController();
    }

    public void persistUser(User user, int countryISO, List<Role> roles, List<Permission> permissions) throws Exception {
        Country where = new Country();
        where.setIsoCode(countryISO);

        Country country = selectEntity(Country.class, where);

        if(country != null){
            user.setCountry(country);
        }else{
            throw new Exception("false country iso");
        }

        user.setRoles(rolesController.getRoles(roles));
        user.setPermission(permissionController.getPermissions(permissions));

        persistObject(user);
    }

    public void persist(User user, int countryISO) throws Exception {
        Country where = new Country();
        where.setIsoCode(countryISO);

        Country country = selectEntity(Country.class, where);

        if(country != null){
            user.setCountry(country);
        }else{
            throw new Exception("false country iso");
        }

        persistObject(user);
    }
}

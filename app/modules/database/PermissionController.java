package modules.database;

import be.objectify.deadbolt.core.models.Permission;
import controllers.security.OcrPermission;
import modules.database.entities.User;
import modules.database.entities.UserPermission;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 01.12.15.
 */
public class PermissionController extends DatabaseController<User, UserPermission>{


    public List<UserPermission> getPermissions(List<Permission> permissions) {
        return permissions.stream().map(permission -> (getPermission(permission))).collect(Collectors.toList());
    }

    public UserPermission getPermission(Permission permission){
        UserPermission rc = new UserPermission();
        if(permission.getValue().equals(OcrPermission.NONE.getValue())){
            rc.setValue(OcrPermission.NONE);
        }else if(permission.getValue().equals(OcrPermission.FULL.getValue())){
            rc.setValue(OcrPermission.FULL);
        }

        return rc;
    }
}

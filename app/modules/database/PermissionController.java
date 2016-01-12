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
public class PermissionController extends DatabaseController<UserPermission, Object>{

    /**
     * liefert alle persistierter userpermissions, die in der übergebenen liste vorhanden sind
     * @param permissions liste von ocrpermissions, die selektiert werden sollten
     * @return liste persistierter userpermissions
     */
    public List<UserPermission> getPermissions(List<OcrPermission> permissions) {
        return permissions.stream().map(permission -> (getPermission(permission))).collect(Collectors.toList());
    }

    /**
     * liefert die userpermission zur übergebenen ocrpermission zurück
     * @param permission benötigte ocrpermission
     * @return userpermission
     */
    public UserPermission getPermission(OcrPermission permission){
        UserPermission rc = new UserPermission();
        if(permission.getValue().equals(OcrPermission.NONE.getValue())){
            rc.setValue(OcrPermission.NONE);
        }else if(permission.getValue().equals(OcrPermission.CMS.getValue())) {
            rc.setValue(OcrPermission.CMS);
        }else if(permission.getValue().equals(OcrPermission.FULL.getValue())){
            rc.setValue(OcrPermission.FULL);
        }

        return rc;
    }
}

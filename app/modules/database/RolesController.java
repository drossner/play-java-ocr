package modules.database;

import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrRole;
import modules.database.entities.SecurityRole;
import modules.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 01.12.15.
 */
public class RolesController extends DatabaseController<User, SecurityRole>{

    public List<SecurityRole> getRoles(List<OcrRole> roles) {
        return roles.stream().map(role -> (getRole(role))).collect(Collectors.toList());
    }

    public SecurityRole getRole(OcrRole role){
        if(role.getName().equals(OcrRole.USER.getName())){
            return new SecurityRole(OcrRole.USER);
        }else if(role.getName().equals(OcrRole.ADMIN.getName())){
            return new SecurityRole(OcrRole.ADMIN);
        }
        return null;
    }
}

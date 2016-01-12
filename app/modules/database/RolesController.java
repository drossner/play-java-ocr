package modules.database;

import be.objectify.deadbolt.core.models.Role;
import controllers.security.OcrRole;
import modules.database.entities.DomainObject;
import modules.database.entities.SecurityRole;
import modules.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 01.12.15.
 */
public class RolesController extends DatabaseController<SecurityRole, OcrRole>{

    /**
     * selektiert alle persistierter securityroles, die in der übergebenen liste vorhanden sind
     * @param roles liste von ocrroles, die selektiert werden sollten
     * @return liste persistierter securityroles
     */
    public List<SecurityRole> getRoles(List<OcrRole> roles) {
        return roles.stream().map(role -> (getRole(role))).collect(Collectors.toList());
    }

    /**
     * selektiert die persistierte securityrole zur übergebnene ocrrole
     * @param role gesuchte ocrrole
     * @return securityrole
     */
    public SecurityRole getRole(OcrRole role){
        return selectEntity(SecurityRole.class, role);
    }
}

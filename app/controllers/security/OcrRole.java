package controllers.security;

import be.objectify.deadbolt.core.models.Role;

/**
 * Created by Daniel on 25.11.2015.
 */
public enum OcrRole implements Role {
    DEFAULT("DEFAULT"),
    USER("USER"),
    ADMIN("ADMIN");

    OcrRole(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String getName() {
        return name;
    }
}

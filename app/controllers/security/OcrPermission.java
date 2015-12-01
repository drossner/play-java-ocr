package controllers.security;

import be.objectify.deadbolt.core.models.Permission;

/**
 * Created by Daniel on 25.11.2015.
 */
public enum OcrPermission implements Permission {
    NONE("none"),
    FULL("full");

    OcrPermission(String value) {
        this.value = value;
    }

    private String value;

    @Override
    public String getValue() {
        return value;
    }
}

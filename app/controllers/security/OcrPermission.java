package controllers.security;

import be.objectify.deadbolt.core.models.Permission;

/**
 * Created by Daniel on 25.11.2015.
 */
public enum OcrPermission implements Permission {
    NONE("NONE"),
    CMS("CMS"),
    FULL("FULL");

    OcrPermission(String value) {
        this.value = value;
    }

    private final String value;

    @Override
    public final String getValue() {
        return value;
    }
}

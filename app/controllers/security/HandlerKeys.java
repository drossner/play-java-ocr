package controllers.security;

import be.objectify.deadbolt.java.ConfigKeys;

/**
 * Created by Daniel on 21.11.2015.
 */
public enum HandlerKeys
{
    DEFAULT(ConfigKeys.DEFAULT_HANDLER_KEY);

    public final String key;

    private HandlerKeys(final String key)
    {
        this.key = key;
    }
}
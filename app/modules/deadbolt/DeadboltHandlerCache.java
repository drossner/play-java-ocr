package modules.deadbolt;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.Singleton;
import controllers.security.HandlerKeys;
import controllers.security.OcrDeadboltHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 21.11.2015.
 */
@Singleton
public class DeadboltHandlerCache implements HandlerCache{

    private final DeadboltHandler defaultHandler = new OcrDeadboltHandler();
    private final Map<String, DeadboltHandler> handlers = new HashMap<>();

    public DeadboltHandlerCache()
    {
        handlers.put(HandlerKeys.DEFAULT.key, defaultHandler);
    }

    @Override
    public DeadboltHandler apply(final String key)
    {
        return handlers.get(key);
    }

    @Override
    public DeadboltHandler get()
    {
        return defaultHandler;
    }

}

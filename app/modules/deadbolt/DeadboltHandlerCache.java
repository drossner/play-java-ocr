package modules.deadbolt;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.Singleton;
import controllers.security.HandlerKeys;
import controllers.security.OcrDeadboltHandler;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 21.11.2015.
 * Custom Deadbolt HandlerCache
 */
@Singleton
public class DeadboltHandlerCache implements HandlerCache{

    private final DeadboltHandler defaultHandler; // = new OcrDeadboltHandler();
    private final Map<String, DeadboltHandler> handlers = new HashMap<>();

    @Inject
    public DeadboltHandlerCache(OcrDeadboltHandler ocrDeadboltHandler)
    {
        this.defaultHandler = ocrDeadboltHandler;
        handlers.put(HandlerKeys.DEFAULT.key, defaultHandler);
    }

    /**
     * @inheritDoc
     */
    @Override
    public DeadboltHandler apply(final String key)
    {
        return handlers.get(key);
    }

    /**
     * @inheritDoc
     */
    @Override
    public DeadboltHandler get()
    {
        return defaultHandler;
    }

}

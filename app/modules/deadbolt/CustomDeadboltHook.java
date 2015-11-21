package modules.deadbolt;

import be.objectify.deadbolt.java.cache.HandlerCache;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

import javax.inject.Singleton;

/**
 * Created by Daniel on 21.11.2015.
 */
public class CustomDeadboltHook extends Module
{
    @Override
    public Seq<Binding<?>> bindings(final Environment environment,
                                    final Configuration configuration)
    {
        return seq(bind(HandlerCache.class).to(DeadboltHandlerCache.class).in(Singleton.class));
    }
}

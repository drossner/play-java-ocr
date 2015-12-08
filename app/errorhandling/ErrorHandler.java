package errorhandling;

import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by daniel on 08.12.15.
 */
public class ErrorHandler extends DefaultHttpErrorHandler{

    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    @Override
    protected F.Promise<Result> onProdServerError(Http.RequestHeader request, UsefulException exception) {
        return F.Promise.<Result>pure(
                Results.internalServerError("A server error occurred: " + exception.getMessage())
        );
    }

    @Override
    protected F.Promise<Result> onForbidden(Http.RequestHeader request, String message) {
        return F.Promise.<Result>pure(
                Results.forbidden("You're not allowed to access this resource.")
        );
    }


}

import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

import business.usermanagement.UserException;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;


/**
 * Global actions such as error handling
 * -> Will be called by PlayFramework!
 */
public class Global extends GlobalSettings {


    @Override
    public void onStart(Application app) {
        super.onStart(app);

        Logger.info("Starting application");
    }


    @Override
    public void onStop(Application app) {
        super.onStop(app);

        Logger.info("Stopping application");
    }

    /*
    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable throwable) {

        if (throwable instanceof UserException) {
            // ToDo: Message box
            return F.Promise.<Result>pure(internalServerError(views.html.error.render(throwable)));
        }

        return F.Promise.<Result>pure(internalServerError(views.html.error.render(throwable)));
    }*/
}

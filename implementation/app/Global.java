import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Results.internalServerError;

/**
 * Created by david on 03.04.16.
 */
public class Global extends GlobalSettings {

    public Global(){

    }


    @Override
    public void onStart(Application app){
        super.onStart(app);

        Logger.info("Starting application");
    }


    @Override
    public void onStop(Application app){
        super.onStop(app);

        Logger.info("Stopping application");
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
        return F.Promise.<Result>pure(internalServerError(views.html.error.render(t)));
    }
}

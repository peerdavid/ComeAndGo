import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

import business.timetracking.TimeTracking;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import business.usermanagement.UserManagement;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;


/**
 * Global actions such as error handling
 * -> Will be called by PlayFramework!
 */
public class Global extends GlobalSettings {


    @Override
    public void onStart(Application app) {
        super.onStart(app);

        Logger.info("Starting application");

        createDatabaseForTests();
    }


    private void createDatabaseForTests() {
        try {
            Injector injector = Guice.createInjector(
                    new infrastructure.Module(),
                    new business.timetracking.Module(),
                    new business.notification.Module(),
                    new business.usermanagement.Module(),
                    new business.reporting.Module(),
                    new business.Module());
            UserManagement userManagement = injector.getInstance(UserManagement.class);
            TimeTracking timeTracking = injector.getInstance(TimeTracking.class);

            // Create test objects
            User admin = userManagement.readUser("admin");
            timeTracking.createTimeTrack(admin.getId(), DateTime.now().minusHours(2), DateTime.now());
        } catch (Exception e){
            e.printStackTrace();
        }
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
    }
    */
}

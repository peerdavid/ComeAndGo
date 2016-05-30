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

            // Create test users
            List<User> users = new ArrayList<>();
            User admin = userManagement.readUser("sebastian");
            User david = new User("david", "test1234", SecurityRole.ROLE_BOSS, "David", "Peer", "peer@comego.at", true, admin, 8.0);
            User patrick = new User("patrick", "test1234", SecurityRole.ROLE_USER, "Patrick", "Summerer", "summerer@comego.at", true, david, 8.0);
            User stefan = new User("stefan", "test1234", SecurityRole.ROLE_PERSONNEL_MANAGER, "Stefan", "Haberl", "haberl@comego.at", true, david, 3.0);
            User leo = new User("leo", "test1234", SecurityRole.ROLE_USER, "Leonhard", "Haas", "haas@comego.at", true, david, 8.0);
            User martin = new User("martin", "test1234", SecurityRole.ROLE_USER, "Martin", "Brunner", "brunner@comego.at", true, david, 8.0);

            david.setBoss(david);
            admin.setBoss(david);

            users.add(admin);
            users.add(david);
            users.add(patrick);
            users.add(stefan);
            users.add(leo);
            users.add(martin);

            for (User u : users) {
                u.setEntryDate(new DateTime(2016, 5, 1, 8, 0));
                if (!u.getUsername().equals("sebastian")) {
                    userManagement.createUser(u);
                }
            }

            // Create timetracks upto today for all users


        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onStop(Application app) {
        super.onStop(app);

        Logger.info("Stopping application");
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable throwable) {

        if (throwable instanceof UserException) {
            // ToDo: Message box
            return F.Promise.<Result>pure(internalServerError(views.html.error.render(throwable)));
        }

        return F.Promise.<Result>pure(internalServerError(views.html.error.render(throwable)));
    }

}

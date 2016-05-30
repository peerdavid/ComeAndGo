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
import org.joda.time.Days;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import utils.DateTimeUtils;

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
            User patrick = new User("patrick", "test1234", SecurityRole.ROLE_USER, "Patrick", "Summerer", "summerer@comego.at", true, admin, 8.0);
            User stefan = new User("stefan", "test1234", SecurityRole.ROLE_PERSONNEL_MANAGER, "Stefan", "Haberl", "haberl@comego.at", true, admin, 8.0);
            User leo = new User("leonhard", "test1234", SecurityRole.ROLE_USER, "Leonhard", "Haas", "haas@comego.at", true, admin, 8.0);
            User martin = new User("martin", "test1234", SecurityRole.ROLE_USER, "Martin", "Brunner", "brunner@comego.at", true, admin, 8.0);

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
                    u.setBoss(david);
                    userManagement.updateUser(u.getUsername(), u);
                }
            }


            // Create timetracks upto today for all users
            // Only from Monday to Friday 08:00 - 17:00 and break from 12:00 to 13:00
            for (User u : users) {
                DateTime entry = u.getEntryDate();
                DateTime today = DateTime.now();

                int dayDifference = Days.daysBetween(entry.toLocalDate(), today.toLocalDate()).getDays();
                for (int i = 0; i <= dayDifference; i++) {
                    if (entry.plusDays(i).getDayOfWeek() != 6 && entry.plusDays(i).getDayOfWeek() != 7) {
                        DateTime from = new DateTime(entry.plusDays(i).getYear(), entry.plusDays(i).getMonthOfYear(), entry.plusDays(i).getDayOfMonth(), 8, 0);
                        DateTime to = new DateTime(entry.plusDays(i).getYear(), entry.plusDays(i).getMonthOfYear(), entry.plusDays(i).getDayOfMonth(), 16, 59, 59);
                        timeTracking.createTimeTrack(u.getId(), from, to, u.getId(), Messages.get("notifications.created_timetrack", u.getFirstName() , DateTimeUtils.dateTimeToDateString(from)));

                    }
                }

                List<TimeTrack> timeTracks = timeTracking.readTimeTracks(u.getId());
                for (TimeTrack t : timeTracks) {
                    DateTime from = new DateTime(t.getFrom().getYear(), t.getFrom().getMonthOfYear(), t.getFrom().getDayOfMonth(), 12, 0);
                    DateTime to = new DateTime(t.getFrom().getYear(), t.getFrom().getMonthOfYear(), t.getFrom().getDayOfMonth(), 12, 59, 59);
                    t.addBreak(new Break(from, to));
                    timeTracking.updateTimeTrack(t, u.getId(), Messages.get("notifications.changed_timetrack", u.getFirstName() , DateTimeUtils.dateTimeToDateString(from)));
                }
            }



        } catch (Exception e) {
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

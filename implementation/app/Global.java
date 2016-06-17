import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

import business.timetracking.TimeOffType;
import business.timetracking.TimeTrackException;
import business.timetracking.TimeTracking;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import business.usermanagement.UserManagement;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.NotificationRepository;
import infrastructure.TimeOffRepository;
import infrastructure.TimeTrackingRepository;
import models.*;
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
import java.util.concurrent.ThreadLocalRandom;


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
            NotificationRepository notificationRepository = injector.getInstance(NotificationRepository.class);
            TimeOffRepository timeOffRepository = injector.getInstance(TimeOffRepository.class);

            // Create test users
            List<User> users = new ArrayList<>();
            User sebastian = userManagement.readUser("sebastian");
            User david = new User("david", "test1234", SecurityRole.ROLE_BOSS, "David", "Peer", "peer@comego.at", true, sebastian, 8.0);
            User patrick = new User("patrick", "test1234", SecurityRole.ROLE_USER, "Patrick", "Summerer", "summerer@comego.at", true, sebastian, 8.0);
            User stefan = new User("stefan", "test1234", SecurityRole.ROLE_PERSONNEL_MANAGER, "Stefan", "Haberl", "haberl@comego.at", true, sebastian, 8.0);
            User leo = new User("leonhard", "test1234", SecurityRole.ROLE_USER, "Leonhard", "Haas", "haas@comego.at", true, sebastian, 8.0);
            User martin = new User("martin", "test1234", SecurityRole.ROLE_USER, "Martin", "Brunner", "brunner@comego.at", true, sebastian, 8.0);

            users.add(sebastian);
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
            sebastian.setBoss(david);
            userManagement.updateUser(sebastian.getUsername(), sebastian);

            // Create some bank holidays for year 2016
            timeTracking.createBankHoliday(sebastian.getId(), new DateTime(2016, 5, 5, 8, 0), new DateTime(2016, 5, 5, 17, 0), "Christi Himmelfahrt");
            timeTracking.createBankHoliday(sebastian.getId(), new DateTime(2016, 5, 16, 8, 0), new DateTime(2016, 5, 16, 17, 0), "Pfingstmontag");
            timeTracking.createBankHoliday(sebastian.getId(), new DateTime(2016, 5, 26, 8, 0), new DateTime(2016, 5, 26, 17, 0), "Fronleichnam");
            timeTracking.createBankHoliday(sebastian.getId(), new DateTime(2016, 8, 15, 8, 0), new DateTime(2016, 8, 15, 17, 0), "Mariä Himmelfahrt");

            // Create timetracks upto today for all users
            // Only from Monday to Friday 08:00 - 17:00 and break from 12:00 to 13:00
            for (User u : users) {
                DateTime entry = u.getEntryDate();
                DateTime now = DateTime.now();

                int dayDifference = Days.daysBetween(entry.toLocalDate(), now.toLocalDate()).getDays();
                for (int i = 0; i < dayDifference; i++) {
                    if (entry.plusDays(i).getDayOfWeek() != 6 && entry.plusDays(i).getDayOfWeek() != 7 && !isBankHoliday(sebastian, entry.plusDays(i))) {
                        int randomMinuteBias = ThreadLocalRandom.current().nextInt(-30, 30 + 1);
                        DateTime from = new DateTime(entry.plusDays(i).getYear(), entry.plusDays(i).getMonthOfYear(), entry.plusDays(i).getDayOfMonth(), 8, 0, 0);
                        from = randomMinuteBias > 0 ? from.plusMinutes(randomMinuteBias) : from.minusMinutes(randomMinuteBias);
                        DateTime to = new DateTime(entry.plusDays(i).getYear(), entry.plusDays(i).getMonthOfYear(), entry.plusDays(i).getDayOfMonth(), 16, 59, 59);
                        randomMinuteBias = ThreadLocalRandom.current().nextInt(-30, 30 + 1);
                        to = randomMinuteBias > 0 ? to.plusMinutes(randomMinuteBias) : to.minusMinutes(randomMinuteBias);
                        timeTracking.createTimeTrack(u.getId(), from, to, stefan.getId(), Messages.get("notifications.created_timetrack", u.getFirstName() , DateTimeUtils.dateTimeToDateString(from)));
                    }
                }



                // Timetracks for today
                if (now.getDayOfWeek() != 6 && now.getDayOfWeek() != 7 && !isBankHoliday(sebastian, now)) {
                    if (now.getHourOfDay() > 16) {
                        DateTime from = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 8, 0, 0);
                        DateTime to = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 16, 59, 59);
                        timeTracking.createTimeTrack(u.getId(), from, to, stefan.getId(), Messages.get("notifications.created_timetrack", u.getFirstName() , DateTimeUtils.dateTimeToDateString(from)));
                    }
                }

                // Add breaks
                List<TimeTrack> timeTracks = timeTracking.readTimeTracks(u.getId());
                for (TimeTrack t : timeTracks) {
                    DateTime from = new DateTime(t.getFrom().getYear(), t.getFrom().getMonthOfYear(), t.getFrom().getDayOfMonth(), 12, 0);
                    DateTime to = new DateTime(t.getFrom().getYear(), t.getFrom().getMonthOfYear(), t.getFrom().getDayOfMonth(), 12, 59, 59);
                    t.addBreak(new Break(from, to));
                    timeTracking.updateTimeTrack(t, stefan.getId(), Messages.get("notifications.changed_timetrack", u.getFirstName() , DateTimeUtils.dateTimeToDateString(from)));
                }

                // Delete unnecessary generated Notifications
                List<Notification> unseen = notificationRepository.readUnseenNotifications(u);
                unseen.forEach(notificationRepository::deleteNotification);
            }

            // Create some holidays, etc.
            timeTracking.requestHoliday(patrick.getId(), new DateTime(2016, 7, 15, 0, 0), new DateTime(2016, 7, 29, 23, 59), "Summer holiday");
            timeTracking.requestHoliday(sebastian.getId(), new DateTime(2016, 8, 1, 0, 0), new DateTime(2016, 8, 21, 23, 59), "Italy");
            timeTracking.requestHoliday(leo.getId(), new DateTime(2016, 7, 2, 0, 0), new DateTime(2016, 7, 12, 23, 59), "I need holiday");

            List<TimeOff> waldiHoliday = timeOffRepository.readTimeOffs(sebastian);
            int waldiTimeOffid = 0;
            for (TimeOff t : waldiHoliday) {
                waldiTimeOffid = t.getId();
                timeTracking.acceptHoliday(waldiTimeOffid, sebastian.getBoss().getId());
            }

            List<TimeOff> leoHoliday = timeOffRepository.readTimeOffs(leo);
            int leoTimeOffid = 0;
            for (TimeOff t : leoHoliday) {
                leoTimeOffid = t.getId();
                timeTracking.acceptHoliday(leoTimeOffid, leo.getBoss().getId());
            }

            // Alter Notifications for boss david
            List<Notification> unseen = notificationRepository.readUnseenNotifications(david);
            for (Notification n : unseen) {
                if (n.getReferenceId().equals(waldiTimeOffid) || n.getReferenceId().equals(leoTimeOffid)) {
                    notificationRepository.markAsRead(n);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBankHoliday(User user, DateTime date) {
        Injector injector = Guice.createInjector(
                new infrastructure.Module(),
                new business.timetracking.Module(),
                new business.notification.Module(),
                new business.usermanagement.Module(),
                new business.reporting.Module(),
                new business.Module());
        TimeOffRepository timeOffRepository = injector.getInstance(TimeOffRepository.class);

        List<TimeOff> timeoffs = null;
        try {
            timeoffs = timeOffRepository.readTimeOffs(user);
        } catch (TimeTrackException e) {
            e.printStackTrace();
        }
        for (TimeOff t : timeoffs) {
            if (t.getType() == TimeOffType.BANK_HOLIDAY && t.getFrom().getYear() == date.getYear() &&
                    t.getFrom().getMonthOfYear() == date.getMonthOfYear() &&
                    t.getFrom().getDayOfYear() == date.getDayOfYear()) {
                return true;
            }
        }
        return false;
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

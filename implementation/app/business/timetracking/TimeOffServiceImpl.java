package business.timetracking;

import business.notification.NotificationSender;
import business.notification.NotificationType;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.TimeOffRepository;
import models.Notification;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

/**
 * Created by paz on 24.04.16.
 */
class TimeOffServiceImpl implements TimeOffService {

    private final TimeOffRepository _repository;
    private final NotificationSender _notificationSender;
    private final InternalUserManagement _userManagement;


    @Inject
    public TimeOffServiceImpl(TimeOffRepository repository, NotificationSender notificationSender, InternalUserManagement userRepository) {
        _repository = repository;
        _notificationSender = notificationSender;
        _userManagement = userRepository;
    }

    @Override
    // ToDo: Validation
    public void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        TimeOff sickLeave = new TimeOff(employee, from, to, TimeOffType.SICK_LEAVE, TimeOffState.DONE, comment);

        _repository.createTimeOff(sickLeave);
        _notificationSender.sendNotification(new Notification(NotificationType.SICK_LEAVE_INFORMATION, comment, employee, boss));

    }

    @Override
    public void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        TimeOff businessTrip = new TimeOff(employee, from, to, TimeOffType.BUSINESS_TRIP, TimeOffState.DONE, comment);

        _repository.createTimeOff(businessTrip);
        _notificationSender.sendNotification(new Notification(NotificationType.BUSINESS_TRIP_INFORMATION, comment, employee, boss));
    }

    @Override
    public void requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        TimeOff timeOff = new TimeOff(employee, from, to, TimeOffType.HOLIDAY, TimeOffState.NEW, comment);
        _repository.createTimeOff(timeOff);

        _notificationSender.sendNotification(new Notification(NotificationType.HOLIDAY_REQUEST, comment, employee, boss));
        timeOff.setState(TimeOffState.REQUEST_SENT);

        _repository.updateTimeOff(timeOff);

    }

    @Override
    public void requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        TimeOff timeOff = new TimeOff(employee, from, to, TimeOffType.SPECIAL_HOLIDAY, TimeOffState.NEW, comment);
        _repository.createTimeOff(timeOff);

        _notificationSender.sendNotification(new Notification(NotificationType.SPECIAL_HOLIDAY_REQUEST, comment, employee, boss));
        timeOff.setState(TimeOffState.REQUEST_SENT);

        _repository.updateTimeOff(timeOff);
    }

    @Override
    public void acceptHoliday(int timeOffId, int bossId) throws Exception {
        // todo: validation and tests
        TimeOff timeOffToAccept = _repository.readTimeOff(timeOffId);
        User employee = timeOffToAccept.getUser();
        User boss = _userManagement.readUser(bossId);

        timeOffToAccept.setState(TimeOffState.REQUEST_ACCEPTED);
        timeOffToAccept.setReviewedBy(boss);
        _repository.updateTimeOff(timeOffToAccept);

        Notification answerToEmployee = new Notification(NotificationType.HOLIDAY_ACCEPT, boss, employee);
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void rejectHoliday(int timeOffId, int bossId) throws Exception {
        // todo: validation and tests
        TimeOff timeOffToReject = _repository.readTimeOff(timeOffId);
        User employee = timeOffToReject.getUser();
        User boss = _userManagement.readUser(bossId);

        timeOffToReject.setState(TimeOffState.REQUEST_REJECTED);
        timeOffToReject.setReviewedBy(boss);
        _repository.updateTimeOff(timeOffToReject);

        Notification answerToEmployee = new Notification(NotificationType.HOLIDAY_REJECT, boss, employee);
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void takeParentalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {

    }

    @Override
    public void requestEducationalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {

    }

    @Override
    public void acceptSpecialHoliday(int timeOffId, int bossId) throws Exception {

    }

    @Override
    public void rejectSpecialHoliday(int timeOffId, int bossId) throws Exception {

    }

    @Override
    public void acceptEducationalLeave(int timeOffId, int bossId) throws Exception {

    }

    @Override
    public void rejectEducationalLeave(int timeOffId, int bossId) throws Exception {

    }
}

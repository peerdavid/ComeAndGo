package business.timetracking;

import business.notification.NotificationSender;
import business.notification.NotificationType;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.NotAuthorizedException;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.TimeOffRepository;
import models.Notification;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by paz on 24.04.16.
 */
class TimeOffServiceImpl implements TimeOffService {

    private final TimeOffRepository _repository;
    private final TimeOffValidation _timeOffValidation;
    private final NotificationSender _notificationSender;
    private final InternalUserManagement _userManagement;


    @Inject
    public TimeOffServiceImpl(TimeOffRepository repository, TimeOffValidation validation, NotificationSender notificationSender, InternalUserManagement userRepository) {
        _repository = repository;
        _timeOffValidation = validation;
        _notificationSender = notificationSender;
        _userManagement = userRepository;
    }

    @Override
    public void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff sickLeave = new TimeOff(employee, from, to, TimeOffType.SICK_LEAVE, RequestState.DONE, comment);

        int id = _repository.createTimeOff(sickLeave);
        _notificationSender.sendNotification(new Notification(NotificationType.SICK_LEAVE_INFORMATION, comment, employee, boss, id));

    }

    @Override
    public void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff businessTrip = new TimeOff(employee, from, to, TimeOffType.BUSINESS_TRIP, RequestState.DONE, comment);

        int id = _repository.createTimeOff(businessTrip);
        _notificationSender.sendNotification(new Notification(NotificationType.BUSINESS_TRIP_INFORMATION, comment, employee, boss, id));
    }

    @Override
    public void requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff timeOff = new TimeOff(employee, from, to, TimeOffType.HOLIDAY, RequestState.REQUEST_SENT, comment);
        int id = _repository.createTimeOff(timeOff);

        Notification notification = new Notification(NotificationType.HOLIDAY_REQUEST, comment, employee, boss, id);

        _notificationSender.sendNotification(notification);
    }

    @Override
    public void requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff timeOff = new TimeOff(employee, from, to, TimeOffType.SPECIAL_HOLIDAY, RequestState.REQUEST_SENT, comment);
        int id = _repository.createTimeOff(timeOff);

        Notification notification = new Notification(NotificationType.SPECIAL_HOLIDAY_REQUEST, comment, employee, boss, id);
        _notificationSender.sendNotification(notification);
    }

    @Override
    public void acceptHoliday(int timeOffId, int bossId) throws Exception {
        TimeOff timeOffToAccept = _repository.readTimeOff(timeOffId);

        // before accepting holiday see if there is no other already accepted timeOff
        _timeOffValidation.validateTimeOff(timeOffToAccept.getUser(), timeOffToAccept.getFrom(), timeOffToAccept.getTo());

        User employee = timeOffToAccept.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        timeOffToAccept.setState(RequestState.REQUEST_ACCEPTED);
        timeOffToAccept.setReviewedBy(boss);
        _repository.updateTimeOff(timeOffToAccept);

        Notification answerToEmployee = new Notification(NotificationType.HOLIDAY_ACCEPT, boss, employee, timeOffToAccept.getId());
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void rejectHoliday(int timeOffId, int bossId) throws Exception {
        TimeOff timeOffToReject = _repository.readTimeOff(timeOffId);
        User employee = timeOffToReject.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        timeOffToReject.setState(RequestState.REQUEST_REJECTED);
        timeOffToReject.setReviewedBy(boss);
        _repository.updateTimeOff(timeOffToReject);

        Notification answerToEmployee = new Notification(NotificationType.HOLIDAY_REJECT, boss, employee, timeOffToReject.getId());
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void takeParentalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff parentalLeave = new TimeOff(employee, from, to, TimeOffType.PARENTAL_LEAVE, RequestState.REQUEST_SENT, comment);
        int id = _repository.createTimeOff(parentalLeave);

        Notification answerToEmployee = new Notification(NotificationType.PARENTAL_LEAVE_REQUEST, comment, employee, boss, id);
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void requestEducationalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        User employee = _userManagement.readUser(userId);
        User boss = employee.getBoss();

        _timeOffValidation.validateTimeOff(employee, from, to);

        TimeOff educationalLeave = new TimeOff(employee, from, to, TimeOffType.EDUCATIONAL_LEAVE, RequestState.REQUEST_SENT, comment);
        int id = _repository.createTimeOff(educationalLeave);

        Notification answerToEmployee = new Notification(NotificationType.EDUCATIONAL_LEAVE_REQUEST, comment, employee, boss, id);
        _notificationSender.sendNotification(answerToEmployee);
    }

    @Override
    public void acceptSpecialHoliday(int timeOffId, int bossId) throws Exception {
        TimeOff requestedTimeOff = _repository.readTimeOff(timeOffId);

        // before accepting special holiday see if there is no other already accepted timeOff
        _timeOffValidation.validateTimeOff(requestedTimeOff.getUser(), requestedTimeOff.getFrom(), requestedTimeOff.getTo());

        User employee = requestedTimeOff.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        requestedTimeOff.setState(RequestState.REQUEST_ACCEPTED);
        requestedTimeOff.setReviewedBy(boss);
        _repository.updateTimeOff(requestedTimeOff);

        Notification notification = new Notification(NotificationType.SPECIAL_HOLIDAY_ACCEPT, boss, employee, requestedTimeOff.getId());
        _notificationSender.sendNotification(notification);
    }

    @Override
    public void rejectSpecialHoliday(int timeOffId, int bossId) throws Exception {
        TimeOff requestedTimeOff = _repository.readTimeOff(timeOffId);
        User employee = requestedTimeOff.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        requestedTimeOff.setState(RequestState.REQUEST_ACCEPTED);
        requestedTimeOff.setReviewedBy(boss);
        _repository.updateTimeOff(requestedTimeOff);

        Notification notification = new Notification(NotificationType.SPECIAL_HOLIDAY_REJECT, boss, employee, requestedTimeOff.getId());
        _notificationSender.sendNotification(notification);
    }

    @Override
    public void acceptEducationalLeave(int timeOffId, int bossId) throws Exception {
        TimeOff requestedTimeOff = _repository.readTimeOff(timeOffId);

        // before accepting educational leave see if there is no other already accepted timeOff
        _timeOffValidation.validateTimeOff(requestedTimeOff.getUser(), requestedTimeOff.getFrom(), requestedTimeOff.getTo());

        User employee = requestedTimeOff.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        requestedTimeOff.setState(RequestState.REQUEST_ACCEPTED);
        requestedTimeOff.setReviewedBy(boss);
        _repository.updateTimeOff(requestedTimeOff);

        Notification notification = new Notification(NotificationType.EDUCATIONAL_LEAVE_ACCEPT, boss, employee, requestedTimeOff.getId());
        _notificationSender.sendNotification(notification);
    }

    @Override
    public void rejectEducationalLeave(int timeOffId, int bossId) throws Exception {
        TimeOff requestedTimeOff = _repository.readTimeOff(timeOffId);
        User employee = requestedTimeOff.getUser();
        User boss = employee.getBoss();

        if (bossId != boss.getId()) {
            throw new NotAuthorizedException("Boss is not boss of employee.");
        }

        requestedTimeOff.setState(RequestState.REQUEST_REJECTED);
        requestedTimeOff.setReviewedBy(boss);
        _repository.updateTimeOff(requestedTimeOff);

        Notification notification = new Notification(NotificationType.EDUCATIONAL_LEAVE_REJECT, boss, employee, requestedTimeOff.getId());
        _notificationSender.sendNotification(notification);
    }


    @Override
    public TimeOff readTimeOffById(int timeOffId) throws Exception {
        return _repository.readTimeOff(timeOffId);
    }


    @Override
    public List<TimeOff> readTimeOffs(int userId) throws Exception {
        User user = _userManagement.readUser(userId);
        return _repository.readTimeOffs(user);
    }

    @Override
    public void deleteTimeOff(int userId, int id) throws Exception {
        TimeOff timeOffToDelete = _repository.readTimeOff(id);

        if (timeOffToDelete.getUser().getId() != userId) {
            throw new NotAuthorizedException("User " + userId + " is not allowed to delete time off " + id);
        }
        if (timeOffToDelete.getFrom().isBefore(DateTime.now())
                || timeOffToDelete.getTo().isBefore(DateTime.now())) {
            throw new UserException("exceptions.timeoff.error_delete_timoff_in_past");
        }

        User employee = timeOffToDelete.getUser();
        User boss = employee.getBoss();
        Notification notification = new Notification(NotificationType.INFORMATION, "Deleted time off", employee, boss);


        _notificationSender.sendNotification(notification);
        _repository.deleteTimeOff(timeOffToDelete);
    }
}

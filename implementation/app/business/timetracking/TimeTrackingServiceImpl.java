package business.timetracking;


import business.notification.InternalNotificationSender;
import business.notification.NotificationException;
import business.notification.NotificationType;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import infrastructure.TimeTrackingRepository;
import javassist.NotFoundException;
import models.Break;
import models.Notification;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import com.google.inject.Inject;
import play.i18n.Messages;


/**
 * Created by david on 21.03.16.
 */
class TimeTrackingServiceImpl implements TimeTrackingService {

    private final TimeTrackingRepository _repository;
    private final InternalNotificationSender _notificationSender;
    private final InternalUserManagement _userManagement;
    private final TimeTrackingValidation _timeTrackValidation;
    private final TimeOffValidation _timeOffValidation;


    @Inject
    public TimeTrackingServiceImpl(TimeTrackingRepository repository, TimeTrackingValidation validation, TimeOffValidation timeOffValidation, InternalNotificationSender notificationSender, InternalUserManagement userRepository) {
        _repository = repository;
        _notificationSender = notificationSender;
        _userManagement = userRepository;
        _timeTrackValidation = validation;
        _timeOffValidation = timeOffValidation;
    }


    @Override
    public int come(int userId) throws UserException {
        if(isActive(userId)) {
            throw new UserException("exceptions.timetracking.error_user_already_working");
        }
        User user = loadUserById(userId);

        _timeOffValidation.validateComeForDate(user, DateTime.now());
        TimeTrack newTimeTrack = new TimeTrack(user);
        return _repository.createTimeTrack(newTimeTrack);
    }


    @Override
    public void go(int userId) throws UserException, NotFoundException {
        if(!isActive(userId)) {
            throw new UserException("exceptions.timetracking.error_user_not_started_work");
        }

        User user = loadUserById(userId);
        TimeTrack timeTrack = _repository.readActiveTimeTrack(user);
        timeTrack.setTo(DateTime.now());
        _repository.updateTimeTrack(timeTrack);
    }


    @Override
    public boolean isActive(int userId) throws UserException {
        User user = loadUserById(userId);

        // _repository throws exception, if there is no TimeTrack available
        try {
            _repository.readActiveTimeTrack(user);
        } catch(NotFoundException e) {
            return false;
        }

        return true;
    }


    @Override
    public boolean takesBreak(int userId) throws UserException {
        User user = loadUserById(userId);

        try {
            _repository.readActiveBreak(user);
        } catch(NotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void createBreak(int userId) throws UserException, NotFoundException {
        if(takesBreak(userId)) {
            throw new UserException("exceptions.timetracking.user_break_error");
        }
        if(!isActive(userId)) {
            throw new UserException("exceptions.timetracking.user_not_working_and_break");
        }

        User user = loadUserById(userId);
        TimeTrack activeTimeTrack = _repository.readActiveTimeTrack(user);
        activeTimeTrack.addBreak(new Break(DateTime.now()));
        _repository.updateTimeTrack(activeTimeTrack);
    }


    @Override
    public void endBreak(int userId) throws UserException, NotFoundException, TimeTrackException {
        if(!takesBreak(userId)) {
            throw new UserException("exceptions.timetracking.user_break_error");
        }
        if(!isActive(userId)) {
            throw new UserException("exceptions.timetracking.user_not_working_and_break");
        }

        User user = loadUserById(userId);
        Break activeBreak = _repository.readActiveBreak(user);
        activeBreak.setTo(DateTime.now());
        _repository.updateBreak(activeBreak);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws UserException {
        User user = loadUserById(userId);

        return _repository.readTimeTracks(user);
    }

    @Override
    public TimeTrack readTimeTrackById(int id) throws Exception {
        return _repository.readTimeTrack(id);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException {
        User user = loadUserById(userId);
       return _repository.readTimeTracks(user, from, to);
    }


    @Override
    public void createTimeTrack(int userId, DateTime from, DateTime to) throws UserException, NotificationException {
        User user = loadUserById(userId);
        TimeTrack timeTrack = new TimeTrack(user, from, to, Collections.emptyList());
        createTimeTrack(timeTrack);
    }


    @Override
    public void createTimeTrack(TimeTrack timeTrack) throws UserException, NotificationException {
        _timeTrackValidation.validateTimeTrackInsert(timeTrack);
        _timeOffValidation.validateTimeOff(timeTrack.getUser(), timeTrack.getFrom(), timeTrack.getTo());
        int id = _repository.createTimeTrack(timeTrack);

        String comment = Messages.get("notifications.created_timetrack", dateToString(timeTrack.getFrom()));
        Notification notification = new Notification(NotificationType.CREATED_TIMETRACK, comment,
            timeTrack.getUser().getBoss(), timeTrack.getUser(), id);
        _notificationSender.sendNotification(notification);
    }


    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) throws NotificationException {
        String comment = Messages.get("notifications.deleted_timetrack", dateToString(timeTrack.getFrom()));
        Notification notification = new Notification(NotificationType.DELETED_TIMETRACK, comment,
            timeTrack.getUser().getBoss(), timeTrack.getUser());
        _notificationSender.sendNotification(notification);

        _repository.deleteTimeTrack(timeTrack);
    }


    @Override
    public void updateTimeTrack(TimeTrack timeTrack) throws UserException, NotificationException {
        _timeTrackValidation.validateTimeTrackUpdate(timeTrack);
        _timeOffValidation.validateTimeOff(timeTrack.getUser(), timeTrack.getFrom(), timeTrack.getTo());
        _repository.updateTimeTrack(timeTrack);


           String comment = Messages.get("notifications.changed_timetrack", dateToString(timeTrack.getFrom()));
           Notification notification = new Notification(NotificationType.CHANGED_TIMETRACK, comment,
               timeTrack.getUser().getBoss(), timeTrack.getUser(), timeTrack.getId());
           _notificationSender.sendNotification(notification);
    }

    private String dateToString(DateTime date) {
        try {
            StringBuilder sb = new StringBuilder();
            int value;
            if ((value = date.getDayOfMonth()) < 10) {
                sb.append("0");
            }
            sb.append(value + ".");
            if ((value = date.getMonthOfYear()) < 10) {
                sb.append("0");
            }
            sb.append(value + ".");
            sb.append(date.getYear());
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private User loadUserById(int userId) throws UserException {
        User user;

        try {
            user = _userManagement.readUser(userId);
        } catch (Exception e) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        return user;
    }
}

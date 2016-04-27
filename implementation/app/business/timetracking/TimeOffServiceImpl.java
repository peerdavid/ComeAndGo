package business.timetracking;

import business.notification.NotificationSender;
import business.notification.NotificationType;
import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import infrastructure.TimeOffRepository;
import infrastructure.TimeTrackingRepository;
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
        User boss = _userManagement.readUser(employee.getUserNameBoss());

        TimeOff sickleave = new TimeOff(employee, from, to, TimeOffType.SICK_LEAVE, TimeOffState.DONE, comment);

        _repository.createTimeOff(sickleave);
        _notificationSender.sendNotification(new Notification(NotificationType.SICK_LEAVE_INFORMATION, comment, employee, boss));

    }

    @Override
    public void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception {

    }

    @Override
    public void requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {

    }

    @Override
    public void requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {

    }

    @Override
    public void acceptHoliday(int timeOffId, int bossId) throws Exception {

    }

    @Override
    public void rejectHoliday(int timeOffId, int bossId) throws Exception {

    }
}

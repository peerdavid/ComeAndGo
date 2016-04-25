package business.timetracking;

import business.notification.NotificationSender;
import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import infrastructure.TimeTrackingRepository;
import models.TimeOff;
import org.joda.time.DateTime;

/**
 * Created by paz on 24.04.16.
 */
class TimeOffServiceImpl implements TimeOffService {

    private final TimeTrackingRepository _repository;
    private final NotificationSender _notificationSender;
    private final InternalUserManagement _userManagement;


    @Inject
    public TimeOffServiceImpl(TimeTrackingRepository repository, NotificationSender notificationSender, InternalUserManagement userRepository) {
        _repository = repository;
        _notificationSender = notificationSender;
        _userManagement = userRepository;
    }

    @Override
    public void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {

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

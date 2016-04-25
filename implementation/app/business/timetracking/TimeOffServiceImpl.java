package business.timetracking;

import business.notification.NotificationSender;
import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import infrastructure.TimeTrackingRepository;
import models.TimeOff;

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
    public void takeSickLeave(TimeOff sickLeave) throws Exception {

    }

    @Override
    public void takeBusinessTrip(TimeOff trip) throws Exception {

    }

    @Override
    public void requestTimeOff(TimeOff timeoff) throws Exception {

    }

    @Override
    public void acceptTimeOff(TimeOff timeoff) throws Exception {

    }

    @Override
    public void rejectTimeOff(TimeOff timeoff) throws Exception {

    }
}

package business.timetracking;

import business.notification.NotificationSender;
import com.google.inject.Inject;
import domain.Notification;
import domain.TimeTrack;
import domain.User;
import infrastructure.TimeTrackingRepository;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
class TimeTrackingServiceImpl implements TimeTrackingService{

    private TimeTrackingRepository _repository;
    private NotificationSender _notificationSender;

    @Inject
    public TimeTrackingServiceImpl(TimeTrackingRepository repository, NotificationSender notificationSender){
        _repository = repository;
        _notificationSender = notificationSender;
    }


    @Override
    public int come() {
        TimeTrack newTimeTrack = new TimeTrack();
        newTimeTrack.setFrom(DateTime.now());
        _repository.createTimeTrack(newTimeTrack);
        return newTimeTrack.getId();
    }


    @Override
    public void go(int id) throws NotFoundException {
        TimeTrack timeTrack = _repository.readTimeTrack(id);
        timeTrack.setTo(DateTime.now());
        _repository.updateTimeTrack(timeTrack);

        User current = new User(7);
        User boss = new User(4);
        Notification notification = new Notification("Go home", current, boss, false);
        _notificationSender.sendNotification(notification);
    }

    @Override
    public List<TimeTrack> readTimeTracks(User user) {
        return _repository.readTimeTracks(user);
    }
}

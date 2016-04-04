package business.timetracking;


import business.notification.NotificationSender;
import infrastructure.TimeTrackingRepository;
import javassist.NotFoundException;
import model.Notification;
import model.TimeTrack;
import model.User;
import org.joda.time.DateTime;
import java.util.List;
import com.google.inject.Inject;


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
        newTimeTrack.set_from(DateTime.now());
        int newId = _repository.createTimeTrack(newTimeTrack);
        return newId;
    }


    @Override
    public void go(int id) throws NotFoundException {
        TimeTrack timeTrack = _repository.readTimeTrack(id);
        timeTrack.set_to(DateTime.now());
        _repository.updateTimeTrack(timeTrack);

        User current = null;
        User boss = null;
        Notification notification = new Notification("Go home", current, boss, false);
        _notificationSender.sendNotification(notification);
    }

    @Override
    public List<TimeTrack> readTimeTracks(User user) {
        return _repository.readTimeTracks(user);
    }
}

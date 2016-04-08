package business.timetracking;


import business.notification.NotificationSender;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
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
class TimeTrackingServiceImpl implements TimeTrackingService {
    private final TimeTrackingRepository _repository;
    private final NotificationSender _notificationSender;
    private final UserRepository _userRepository;

    @Inject
    public TimeTrackingServiceImpl(TimeTrackingRepository repository, NotificationSender notificationSender, UserRepository userRepository) {
        _repository = repository;
        _notificationSender = notificationSender;
        _userRepository = userRepository;
    }


    @Override
    public int come(int userId) {
        User user = _userRepository.readUser(userId);

        TimeTrack newTimeTrack = new TimeTrack();
        newTimeTrack.set_from(DateTime.now());
        int newId = _repository.createTimeTrack(newTimeTrack);
        return newId;
    }


    @Override
    public void go(int userId) throws NotFoundException {
        User user = _userRepository.readUser(userId);

        TimeTrack timeTrack = _repository.readTimeTrack(user.getId());
        timeTrack.set_to(DateTime.now());
        _repository.updateTimeTrack(timeTrack);

        User current = null;
        User boss = null;
        Notification notification = new Notification("Go home", current, boss, false);
        _notificationSender.sendNotification(notification);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) {
        User user = _userRepository.readUser(userId);

        return _repository.readTimeTracks(user);
    }
}

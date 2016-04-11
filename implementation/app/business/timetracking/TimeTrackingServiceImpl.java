package business.timetracking;


import business.UserException;
import business.notification.NotificationSender;
import infrastructure.TimeTrackException;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
import javassist.NotFoundException;
import model.Break;
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
    public int come(int userId) throws UserException, TimeTrackException {
        User user = loadUserById(userId);

        TimeTrack newTimeTrack = new TimeTrack(user);
        newTimeTrack.set_from(DateTime.now());
        int newId = _repository.createTimeTrack(newTimeTrack, user);
        return newId;
    }


    @Override
    public void go(int userId) throws NotFoundException, UserException {
        User user = loadUserById(userId);

        TimeTrack timeTrack = _repository.getActiveTimeTrack(user);
        timeTrack.set_to(DateTime.now());
        _repository.updateTimeTrack(timeTrack);

/*        User current = null;
        User boss = null;
        Notification notification = new Notification("Go home", current, boss, false);
        _notificationSender.sendNotification(notification);*/
    }

    @Override
    public boolean isActive(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);

        TimeTrack activeTimeTrack = _repository.getActiveTimeTrack(user);

        return activeTimeTrack.get_to() == null;
    }

    @Override
    public boolean takesBreak(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);

        Break activeBreak = _repository.getActiveBreak(user);

        return activeBreak.getTo() == null;
    }

    @Override
    public void createBreak(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);
        _repository.startBreak(user);
    }

    @Override
    public void endBreak(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);
        _repository.endBreak(user);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);

        return _repository.readTimeTracks(user);
    }

    private User loadUserById(int userId) throws UserException {
        User user = _userRepository.readUser(userId);

        if (user == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }

        return user;
    }
}

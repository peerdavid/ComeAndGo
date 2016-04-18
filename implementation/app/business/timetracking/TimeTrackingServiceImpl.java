package business.timetracking;


import business.UserException;
import business.notification.NotificationSender;
import infrastructure.TimeTrackException;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
import javassist.NotFoundException;
import models.Break;
import models.TimeTrack;
import models.User;
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
    public void go(int userId) throws NotFoundException, UserException, TimeTrackException {
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

        // _repository throws exception, if there is no TimeTrack available
        try {
            _repository.getActiveTimeTrack(user);
        } catch(NotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean takesBreak(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);

        try {
            _repository.getActiveBreak(user);
        } catch(NotFoundException e) {
            return false;
        }
        return true; //activeBreak != null && activeBreak.getTo() == null;
    }

    @Override
    public void createBreak(int userId) throws UserException, NotFoundException, TimeTrackException {
        if(takesBreak(userId)) {
            throw new UserException("You already take a break");
        }
        if(!isActive(userId)) {
            throw new UserException("You must be working, otherwise you can't take a break");
        }

        User user = loadUserById(userId);
        _repository.startBreak(user);
    }

    @Override
    public void endBreak(int userId) throws UserException, NotFoundException, TimeTrackException{
        if(!takesBreak(userId)) {
            throw new UserException("You cannot end your break before you start it.");
        }
        if(!isActive(userId)) {
            throw new UserException("You have to be working, otherwise you can't end your break");
        }

        User user = loadUserById(userId);
        _repository.endBreak(user);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws UserException, TimeTrackException {
        User user = loadUserById(userId);

        return _repository.readTimeTracks(user);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException, TimeTrackException {
        User user = loadUserById(userId);
       return _repository.readTimeTracks(user, from, to);
    }

    private User loadUserById(int userId) throws UserException {
        User user = _userRepository.readUser(userId);

        if (user == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }

        return user;
    }

    @Override
    public void addTimeTrack(TimeTrack timeTrack) throws UserException {
        _repository.addTimeTrack(timeTrack);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {
        _repository.deleteTimeTrack(timeTrack);
    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack) {
        _repository.updateTimeTrack(timeTrack);
    }

    @Override
    public void addBreak(TimeTrack timeTrack, Break breakToInsert) throws UserException {
        _repository.addBreak(timeTrack, breakToInsert);
    }

    @Override
    public void deleteBreak(Break breakToDelete) throws TimeTrackException {
        _repository.deleteBreak(breakToDelete);
    }

    @Override
    public void updateBreak(Break breakToUpdate) throws TimeTrackException {
        _repository.updateBreak(breakToUpdate);
    }
}

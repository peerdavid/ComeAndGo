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
    private final TimeTrackingValidation _validation;


    @Inject
    public TimeTrackingServiceImpl(TimeTrackingRepository repository, NotificationSender notificationSender, UserRepository userRepository) {
        _repository = repository;
        _notificationSender = notificationSender;
        _userRepository = userRepository;
        _validation = new TimeTrackingValidationImpl(repository);
    }


    @Override
    public int come(int userId) throws UserException {
        User user = loadUserById(userId);

        TimeTrack newTimeTrack = new TimeTrack(user);
        int newId = _repository.createTimeTrack(newTimeTrack, user);
        return newId;
    }


    @Override
    public void go(int userId) throws UserException, NotFoundException {
        User user = loadUserById(userId);

        TimeTrack timeTrack = _repository.getActiveTimeTrack(user);
        timeTrack.setTo(DateTime.now());
        _repository.updateTimeTrack(timeTrack);
    }


    @Override
    public boolean isActive(int userId) throws UserException {
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
    public boolean takesBreak(int userId) throws UserException {
        User user = loadUserById(userId);

        try {
            _repository.getActiveBreak(user);
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
        _repository.startBreak(user);
    }


    @Override
    public void endBreak(int userId) throws UserException, NotFoundException, TimeTrackException{
        if(!takesBreak(userId)) {
            throw new UserException("exceptions.timetracking.user_break_error");
        }
        if(!isActive(userId)) {
            throw new UserException("exceptions.timetracking.user_not_working_and_break");
        }

        User user = loadUserById(userId);
        _repository.endBreak(user);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws UserException {
        User user = loadUserById(userId);

        return _repository.readTimeTracks(user);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException {
        User user = loadUserById(userId);
       return _repository.readTimeTracks(user, from, to);
    }

    @Override
    public void addTimeTrack(TimeTrack timeTrack) throws UserException {
        if(!_validation.validateTimeTrackInsert(timeTrack)) {
            throw new UserException(".....");
        }
        _repository.addTimeTrack(timeTrack);
    }


    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {
        _repository.deleteTimeTrack(timeTrack);
    }


    @Override
    public void updateTimeTrack(TimeTrack timeTrack) throws UserException {
        if(!_validation.validateTimeTrackUpdate(timeTrack)) {
            throw new UserException("...");
        }
        _repository.updateTimeTrack(timeTrack);
    }


    @Override
    public void addBreak(TimeTrack timeTrack, Break breakToInsert) throws UserException {
        _repository.addBreak(timeTrack, breakToInsert);
    }


    @Override
    public void deleteBreak(Break breakToDelete) {
        _repository.deleteBreak(breakToDelete);
    }


    @Override
    public void updateBreak(Break breakToUpdate) {
        _repository.updateBreak(breakToUpdate);
    }


    private User loadUserById(int userId) throws UserException {
        User user = _userRepository.readUser(userId);

        if (user != null) {
            return user;
        }

        throw new UserException("exceptions.usermanagement.no_such_user");
    }
}

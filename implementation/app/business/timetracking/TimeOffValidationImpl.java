package business.timetracking;

import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.TimeOffRepository;
import infrastructure.TimeTrackingRepository;
import models.TimeOff;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Stefan on 28.04.2016.
 */
class TimeOffValidationImpl implements TimeOffValidation {
    TimeOffRepository _repository;
    TimeTrackingRepository _timeTrackingRepository;

    @Inject
    TimeOffValidationImpl(TimeOffRepository repository, TimeTrackingRepository timeTrackingRepository) {
        _repository = repository;
        _timeTrackingRepository = timeTrackingRepository;
    }

    @Override
    public void validateComeForDate(User user, DateTime date) throws UserException {
        // initialize from with midnight
        DateTime from = DateTimeUtils.startOfDay(date);
        DateTime to = DateTimeUtils.endOfDay(date);
        validateTimeOffForTimeTrackInsert(user, from, to);
    }

    @Override
    public void validateTimeOffForTimeTrackInsert(User user, DateTime from, DateTime to) throws UserException {
        if(from.isBefore(DateTimeUtils.startOfDay(user.getEntryDate()))) {
            throw new UserException("exceptions.timetracking.error_timeTrack_before_users_entry");
        }

        try {
            List<TimeOff> timeOffsFromUser = _repository.readTimeOffsFromUser(user, from, to);

            // at this point user has timeOff(s) in conflict
            StringBuilder sb = new StringBuilder("");

            timeOffsFromUser.forEach(actual -> {
                if(actual.getState() == RequestState.REQUEST_ACCEPTED || actual.getState() == RequestState.DONE) {
                    sb.append(String.format("%s - (%s), ", actual.getType(), actual.getComment()));
                }
            });

            if(sb.toString().equals("")) {
                // if there are only REQUEST_REJECTED and REQUEST_SENT ,... there is no need to throw exception
                return;
            }

            throw new UserException("exceptions.timeoff.error_clashing_timeoffs", sb.toString());

        } catch (TimeTrackException e) {
            // request is in no conflict to others
            return;
        }
    }

    @Override
    public void validateTimeOff(User user, DateTime from, DateTime to) throws UserException {
        validateClashingTimeTracks(user, from, to);
        validateTimeOffForTimeTrackInsert(user, from, to);
    }

    private void validateClashingTimeTracks(User user, DateTime from, DateTime to) throws UserException {
        List<TimeTrack> _timeTracks = _timeTrackingRepository.readTimeTracks(user, from, to);
        if (_timeTracks.size() > 0) {
            StringBuilder sb = new StringBuilder();
            _timeTracks.forEach(actual -> sb.append("[" + DateTimeUtils.dateTimeToDateString(actual.getFrom()) + "] "));
            throw new UserException("exceptions.timetracking.validate_clashing_timetracks", sb.toString());
        }
    }
}

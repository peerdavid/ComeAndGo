package business.timetracking;

import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.TimeOffRepository;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by Stefan on 28.04.2016.
 */
class TimeOffValidationImpl implements TimeOffValidation {
    TimeOffRepository _repository;

    @Inject
    TimeOffValidationImpl(TimeOffRepository repository) {
        _repository = repository;
    }

    @Override
    public void validateComeForDate(User user, DateTime date) throws UserException {
        // initialize from with midnight
        DateTime from = date.minusSeconds(DateTimeConstants.SECONDS_PER_DAY - date.getSecondOfDay());
        DateTime to = from.plusSeconds(DateTimeConstants.SECONDS_PER_DAY - 1);
        validateTimeOff(user, from, to);
    }

    @Override
    public void validateTimeOff(User user, DateTime from, DateTime to) throws UserException {

        try {
            List<TimeOff> timeOffsFromUser = _repository.readTimeOffsFromUser(user, from, to);

            // at this point user has timeOff(s) in conflict
            StringBuilder sb = new StringBuilder("");

            for(TimeOff actual : timeOffsFromUser) {
                if(actual.getState() == RequestState.REQUEST_ACCEPTED || actual.getState() == RequestState.DONE) {
                    sb.append(String.format("%s - (%s), ", actual.getType(), actual.getComment()));
                }
            }

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
}

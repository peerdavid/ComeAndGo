package business.timetracking;

import business.usermanagement.UserException;
import infrastructure.TimeOffRepository;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;
import play.i18n.Messages;

import java.util.List;

/**
 * Created by Stefan on 28.04.2016.
 */
class TimeOffValidationImpl implements TimeOffValidation {
    TimeOffRepository _repository;

    TimeOffValidationImpl(TimeOffRepository repository) {
        _repository = repository;
    }

    @Override
    public void validateTimeOffRequest(User user, DateTime from, DateTime to) throws UserException {
        List<TimeOff> timeOffsFromUser = null;
        try {
            _repository.readTimeOffFromUser(user, from, to);
        } catch (TimeTrackException e) {
            // request is in no conflict to others
            return;
        }

        // at this point user has timeOff(s) in conflict
        StringBuilder sb = new StringBuilder();
        for(TimeOff actual : timeOffsFromUser) {
            sb.append(actual.getType().toString() + ", ");
        }

        throw new UserException(Messages.get("exceptions.timeoff.error_clashing_timeoffs", sb.toString()));
    }
}

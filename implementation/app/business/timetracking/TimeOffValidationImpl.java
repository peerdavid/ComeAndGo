package business.timetracking;

import business.usermanagement.UserException;
import com.google.inject.Inject;
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

    @Inject
    TimeOffValidationImpl(TimeOffRepository repository) {
        _repository = repository;
    }

    @Override
    public void validateTimeOff(User user, DateTime from, DateTime to) throws UserException {

        try {
            List<TimeOff> timeOffsFromUser = _repository.readTimeOffFromUser(user, from, to);

            // at this point user has timeOff(s) in conflict
            StringBuilder sb = new StringBuilder();

            for(TimeOff actual : timeOffsFromUser) {
                if(actual.getState() == TimeOffState.REQUEST_ACCEPTED) {
                    sb.append(String.format("%s - (%s), ", actual.getType(), actual.getComment()));
                }
            }

            if(sb.toString().isEmpty()) {
                // if there are only REQUEST_REJECTED and REQUEST_SENT ,... there is no need to throw exception
                return;
            }

            throw new UserException(Messages.get("exceptions.timeoff.error_clashing_timeoffs", sb.toString()));

        } catch (TimeTrackException e) {
            // request is in no conflict to others
            return;
        }
    }
}

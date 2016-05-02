package business.timetracking;


import business.usermanagement.UserException;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

/**
 * Created by Stefan on 28.04.2016.
 */
interface TimeOffValidation {
    void validateTimeOff(User user, DateTime from, DateTime to) throws UserException;
}

package business.timetracking;

import business.UserException;
import models.Break;
import models.TimeTrack;
import models.User;

/**
 * Created by stefan on 18.04.16.
 */
interface TimeTrackingValidation {
   void validateTimeTrackInsert(TimeTrack timeTrack) throws UserException;

   void validateTimeTrackUpdate(TimeTrack timeTrack) throws UserException;
}

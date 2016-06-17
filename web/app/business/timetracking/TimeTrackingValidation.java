package business.timetracking;

import business.usermanagement.UserException;
import models.TimeTrack;

/**
 * Created by stefan on 18.04.16.
 */
interface TimeTrackingValidation {
   void validateTimeTrackInsert(TimeTrack timeTrack) throws UserException;

   void validateTimeTrackUpdate(TimeTrack timeTrack) throws UserException;
}

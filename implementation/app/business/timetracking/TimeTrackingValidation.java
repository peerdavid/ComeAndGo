package business.timetracking;

import models.Break;
import models.TimeTrack;
import models.User;

/**
 * Created by stefan on 18.04.16.
 */
interface TimeTrackingValidation {
   boolean validateTimeTrackInsert(TimeTrack timeTrack);

   boolean validateTimeTrackUpdate(TimeTrack timeTrack);

   boolean validateBreakInsert(TimeTrack timeTrack, Break breakToInsert);
}

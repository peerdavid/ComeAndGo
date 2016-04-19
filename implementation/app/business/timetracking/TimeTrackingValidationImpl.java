package business.timetracking;

import infrastructure.TimeTrackingRepository;
import javassist.NotFoundException;
import models.Break;
import models.TimeTrack;
import models.User;

import java.util.List;

/**
 * Created by stefan on 18.04.16.
 */
class TimeTrackingValidationImpl implements TimeTrackingValidation {
   private TimeTrackingRepository _repository;

   public TimeTrackingValidationImpl(TimeTrackingRepository repository) {
      _repository = repository;
   }

   @Override
   public boolean validateTimeTrackInsert(TimeTrack timeTrack) {
      User user = timeTrack.getUser();

      // following two lines are dummy - TODO: add maximum working time validation instead of following lines
      //if( ...) {
      //   return false;
      //}
      // TODO: check for user, if he is ill, in holiday, ...

      // this list contains timeTracks which do overlap
      List<TimeTrack> timesFromUser =
          _repository.readTimeTracksOverlay(user, timeTrack);

      return timesFromUser.size() <= 0;
   }

   @Override
   public boolean validateTimeTrackUpdate(TimeTrack timeTrack) {
      // TODO: add validation, if timeTrack lies inside holiday, ill time, ...

      User user = timeTrack.getUser();
      TimeTrack actualTimeTrack;

      // check if the user wants to edit the actual TimeTrack and ONLY the to-time
      //   e.g. in case he has forgot to GO
      try {
         actualTimeTrack = _repository.getActiveTimeTrack(user);
         if(actualTimeTrack.getId() == timeTrack.getId() && actualTimeTrack.getFrom().isEqual(timeTrack.getFrom())) {
            return true;
         }
      } catch (Exception e) {}

/*       now find the list of timeTracks which is overlapping the given one
       but watch out, because the timeTrack to edit can of course overlap itself
       thats because we have to sort out all timeTracks which are ident, only if there is at least one
       which does an overlap, return false*/
      List<TimeTrack> overLayList = _repository.readTimeTracksOverlay(user, timeTrack);
      boolean validate = true;

      for(TimeTrack track : overLayList) {
         if(track.getId() != timeTrack.getId()) {
            validate = false;
         }
      }
      return validate;
   }

   @Override
   public boolean validateBreakInsert(TimeTrack timeTrack, Break breakToInsert) {
      // break has to be in between the times from timeTrack;
      if(timeTrack.getFrom().isAfter(breakToInsert.getFrom())
              || timeTrack.getTo().isBefore(breakToInsert.getTo())) {
         return false;
      }

      // at this point we have to be sure, there is no timetrack when the user is ill or in holiday
      // so we do not have to check this again

      // check for breaks which are overlaping to the other
      List<Break> overlapBreakList = _repository.readBreakListOverlay(timeTrack, breakToInsert);
      return overlapBreakList.size() <= 0;


   }
}

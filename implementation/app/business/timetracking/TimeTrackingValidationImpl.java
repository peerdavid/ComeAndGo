package business.timetracking;

import business.usermanagement.UserException;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.List;

/**
 * Created by stefan on 18.04.16.
 *
 * I know this class looks weird, but I gave my best to not confuse you :-)
 */
class TimeTrackingValidationImpl implements TimeTrackingValidation {
   private TimeTrackingRepository _repository;

   public TimeTrackingValidationImpl(TimeTrackingRepository repository) {
      _repository = repository;
   }

   @Override
   public void validateTimeTrackInsert(TimeTrack timeTrack) throws UserException {
      // TODO: add maximum working time validation, checks for holiday, illness, ...

      // this list contains timeTracks which do overlap, if it is empty, then there is no worry of conflict timeTracks
      User user = timeTrack.getUser();
      List<Break> breakList = timeTrack.getBreaks();
      if(breakList == null) {
         breakList = Collections.emptyList();
      }

      List<TimeTrack> timesFromUser =
          _repository.readTimeTracksOverlay(user, timeTrack);
      if(timesFromUser.size() > 0) {
         throw new UserException("exceptions.timetracking.validate_clashing_timetracks");
      }

      validateBreaks(breakList, timeTrack);
   }

   @Override
   public void validateTimeTrackUpdate(TimeTrack timeTrack) throws UserException {
      // TODO: add validation, if timeTrack lies inside holiday, ill time, ...

      // the following query does the filtering for overlapping timeTracks
      //     after that we have to find out, if the list contains timeTrack itself, because timeTrack
      //     will overlap itself
      User user = timeTrack.getUser();
      List<Break> breakList = timeTrack.getBreaks();
      if(breakList == null) {
         breakList = Collections.emptyList();
      }
      List<TimeTrack> overLayList = _repository.readTimeTracksOverlay(user, timeTrack);

      for(TimeTrack actual : overLayList) {
         if(actual.getId() != timeTrack.getId()) {
            throw new UserException("exceptions.timetracking.validate_clashing_timetracks");
         }
      }

      validateBreaks(breakList, timeTrack);
   }

   private void validateBreaks(List<Break> breakList, TimeTrack timeTrack) throws UserException {
      // now at this point the timeTrack could be inserted
      // we also have to check times from the breaks ...
      for(Break actual : breakList) {
         validateBreakInsideTimeTrack(timeTrack, actual);
      }

      // finally check breaks for not clashing
      validateBreaksDoNotClash(breakList);
   }

   private void validateBreakInsideTimeTrack(TimeTrack timeTrack, Break toInsert) throws UserException {
      LocalTime timeTrackStart = timeTrack.getFrom().toLocalTime();
      LocalTime timeTrackEnd = timeTrack.getTo().toLocalTime();

      LocalTime breakStart = toInsert.getFrom().toLocalTime();
      LocalTime breakEnd = toInsert.getTo().toLocalTime();

      // we have nightWork, if start of work e.g. 7pm is "after" 6am
      //   we can trust that original start and end times in module TimeTrack.class are validated to be start before end
      //   but above DateTime is parsed to LocalTime, which means we have no date any more
      boolean nightWork = timeTrackStart.isAfter(timeTrackEnd);
      if(nightWork) {
         // e.g. break is between 8pm and midnight and also after begin of work
         if(breakStart.isAfter(timeTrackStart) && breakEnd.isAfter(timeTrackStart)) {
            return;
         }
         // e.g. break is between midnight and 7am
         if(breakStart.isBefore(timeTrackEnd) && breakEnd.isBefore(timeTrackEnd)) {
            return;
         }
         if(breakStart.isAfter(timeTrackStart) && breakEnd.isBefore(timeTrackEnd)) {
            return;
         }
         throw new UserException("exceptions.timetracking.validate_break_not_inside_timetrack");

      } else {
         if(breakStart.isBefore(timeTrackStart) || breakEnd.isAfter(timeTrackEnd)) {
            throw new UserException("exceptions.timetracking.validate_break_not_inside_timetrack");
         }
      }
   }

   private void validateBreaksDoNotClash(List<Break> breakList) throws UserException {
      int amountBreaks = breakList.size();
      boolean breakOverMidnight;
      /*
      take first, compare with second, third ...
      take second, compare with third ...
      take third and compare with fourth...
       */
      for(int i = 0; i < amountBreaks; i++) {
         LocalTime actualStart = breakList.get(i).getFrom().toLocalTime();
         LocalTime actualEnd = breakList.get(i).getTo().toLocalTime();
         breakOverMidnight = actualStart.isAfter(actualEnd);

         // if there is a break which lies inside "actual" throw UserException
         for(int j = i + 1; j < amountBreaks; j++) {
            LocalTime toInspectStart = breakList.get(j).getFrom().toLocalTime();
            LocalTime toInspectEnd = breakList.get(j).getTo().toLocalTime();

            if(breakOverMidnight) {
               if(actualStart.isBefore(toInspectEnd) || actualEnd.isAfter(toInspectStart)) {
                  throwClashingBreakException();
               }
            }
            else {
               boolean isToInspectOverMidnight = toInspectStart.isAfter(toInspectEnd);
               if(isToInspectOverMidnight && toInspectStart.isBefore(actualEnd)) {
                  throwClashingBreakException();               }
               if(isToInspectOverMidnight && toInspectEnd.isAfter(actualStart)) {
                  throwClashingBreakException();               }
               if(!isToInspectOverMidnight && toInspectStart.isAfter(actualStart) && toInspectStart.isBefore(actualEnd)) {
                  throwClashingBreakException();               }
               if(!isToInspectOverMidnight && toInspectEnd.isAfter(actualStart) && toInspectEnd.isBefore(actualEnd)) {
                  throwClashingBreakException();               }
            }
         }
      }
   }

   private void throwClashingBreakException() throws UserException {
      throw new UserException("exceptions.timetracking.validate_clashing_breaks");
   }
}

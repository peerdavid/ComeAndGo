package business.timetracking;

import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import utils.DateTimeUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by stefan on 18.04.16.
 *
 * I know this class looks weird, but I gave my best to not confuse you :-)
 */
class TimeTrackingValidationImpl implements TimeTrackingValidation {
   private final TimeTrackingRepository _repository;

   @Inject
   public TimeTrackingValidationImpl(TimeTrackingRepository repository) {
      _repository = repository;
   }

   @Override
   public void validateTimeTrackInsert(TimeTrack timeTrack) throws UserException {
      // this list contains timeTracks which do overlap, if it is empty, then there is no worry of conflict timeTracks
      User user = timeTrack.getUser();
      List<Break> breakList = timeTrack.getBreaks();
      if(breakList == null) {
         breakList = Collections.emptyList();
      }

      List<TimeTrack> timesFromUser =
          _repository.readTimeTracksOverlay(user, timeTrack);
      if(timesFromUser.size() > 0) {
         throwClashingTimeTrackException();
      }

      validateTimeTrackNotBeforeUsersEntry(timeTrack);
      validateTimeTrackNotInFuture(timeTrack);
      validateTimeTrackDuration(timeTrack);
      validateBreaks(breakList, timeTrack);
   }

   @Override
   public void validateTimeTrackUpdate(TimeTrack timeTrack) throws UserException {
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
            throwClashingTimeTrackException();
         }
      }

      validateTimeTrackNotBeforeUsersEntry(timeTrack);
      validateTimeTrackNotInFuture(timeTrack);
      validateTimeTrackDuration(timeTrack);
      validateBreaks(breakList, timeTrack);
   }

   private void validateTimeTrackNotBeforeUsersEntry(TimeTrack timeTrack) throws UserException {
      if(timeTrack.getFrom().isBefore(DateTimeUtils.startOfDay(timeTrack.getUser().getEntryDate()))) {
         throwTimeTrackBeforeEntryDateException();
      }
   }

   private void validateTimeTrackNotInFuture(TimeTrack timeTrack) throws UserException {
      if(timeTrack.getFrom().isAfter(DateTime.now())) {
         throwTimeTrackInFutureException();
      }
   }

   private void validateTimeTrackDuration(TimeTrack timeTrack) throws UserException {
      // at this point from- and to-time cannot be null, because TimeTrack class validates this...
      long minutesDifference = DateTimeUtils.getDateTimeDifferenceInMinutes(
          timeTrack.getTo(), timeTrack.getFrom());
      minutesDifference = minutesDifference < 0 ? -minutesDifference : minutesDifference;
      if((double)minutesDifference / 60 > 24) {
         throwTooLongTimeTrackException();
      }
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
         throwBreakNotInsideTimeTrackException();

      } else {
         if(breakStart.isBefore(timeTrackStart) || breakEnd.isAfter(timeTrackEnd)) {
            throwBreakNotInsideTimeTrackException();
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

            /**
             * breaks can also start at same time...
             */
            if(toInspectStart.isEqual(actualStart) || toInspectEnd.isEqual(actualEnd)) {
               throwClashingBreakException();
            }
            /**
             * if the actualBreak is over midnight, there are several cases to respect
             * (the position marked with # is midnight)
             *
             *             |___________actualBreak____#_________|
             *          |__________1__________|       #  |_2_________|
             *                |______3________|       #  |_4___|
             *                |_________5_____________#________|
             *          |_________________6___________#_______________|
             *          |__________________7__________#________|
             *                |_____________8_________#________|
             *
             *   cases 1,3 can be combined because if toInspectEnd is after actualStart, both cases are respected
             *   cases 2,4 can be combined on a similar way
             *   cases 5-8 can be combined because all of these are over midnight
             */
            else if(breakOverMidnight) {
               boolean isActualBreakOverMidnight = toInspectStart.isAfter(toInspectEnd);
               if((toInspectEnd.isAfter(actualStart))             // cases 1 and 3
                   || (toInspectStart.isBefore(actualEnd))        // cases 2 and 4
                   || isActualBreakOverMidnight) {                // cases 5,6,7,8
                  throwClashingBreakException();
               }
            }
            /**
             * if we have no break which starts before midnight and ends after midnight, there are 4 cases where
             * breaks can clash:
             *
             *           |__________actualBreak_________|
             *
             *
             *       |_____case1____|           |______case2______|
             *       |_________________case3______________________|
             *                |________case4_____|
             *
             *
             *    the validation also checks for the breaks lying over midnight, especially in case1 and case2 these things
             *    have to be validated.
             *    For case3 there are two possibilities (actualBreak cannot be over midnight at this point):
             *
             *             |________actualBreak____|
             *         |_!_____________________________|
             *         |_____________________________!_|
             *
             *    --> midnight can be at the ! marked positions, both cases have to be attended
             *
             *    for case4 we can be sure that it cannot be over midnight, because otherwise also actualBreak would be over midnight
             *    - that's because we only have to inspect the case where break to insert does not be over midnight
             */
            else {
               boolean isToInspectOverMidnight = toInspectStart.isAfter(toInspectEnd);
               // case2 iff the break for insert is over midnight
               if(isToInspectOverMidnight && toInspectStart.isBefore(actualEnd)) {
                  throwClashingBreakException();               }
               // case2 iff the break for insert is not over midnight
               if(!isToInspectOverMidnight && toInspectStart.isBefore(actualEnd) && toInspectEnd.isAfter(actualEnd)) {
                  throwClashingBreakException();               }
               // case1 iff the break for insert is over midnight
               if(isToInspectOverMidnight && toInspectEnd.isAfter(actualStart)) {
                  throwClashingBreakException();               }
               // case1 iff the break for insert is not over midnight
               if(!isToInspectOverMidnight && toInspectEnd.isAfter(actualStart) && toInspectStart.isBefore(actualStart)) {
                  throwClashingBreakException();               }
               // case3  (if break to insert is over midnight)
               if(isToInspectOverMidnight && (toInspectStart.isBefore(actualStart) || toInspectEnd.isAfter(actualEnd))) {
                  throwClashingBreakException();               }
               // case3 (if break to inset is not over midnight)
               if(!isToInspectOverMidnight && toInspectStart.isBefore(actualStart) && toInspectEnd.isAfter(actualEnd)) {
                  throwClashingBreakException();               }
               // case4 (ignore case that break to inspect is over midnight, because for that case also actualBreak has to be over midnight)
               if(!isToInspectOverMidnight && toInspectStart.isAfter(actualStart) && toInspectEnd.isBefore(actualEnd)) {
                  throwClashingBreakException();               }
            }
         }
      }
   }

   private void throwClashingBreakException() throws UserException {
      throw new UserException("exceptions.timetracking.validate_clashing_breaks");
   }

   private void throwBreakNotInsideTimeTrackException() throws UserException {
      throw new UserException("exceptions.timetracking.validate_break_not_inside_timetrack");
   }

   private void throwClashingTimeTrackException() throws UserException {
      throw new UserException("exceptions.timetracking.validate_clashing_timetracks");
   }

   private void throwTooLongTimeTrackException() throws UserException {
      throw new UserException("exceptions.timetracking.error_too_long_timeTrack");
   }

   private void throwTimeTrackInFutureException() throws UserException {
      throw new UserException("exceptions.timetracking.error_timeTrack_in_future");
   }

   private void throwTimeTrackBeforeEntryDateException() throws UserException {
      throw new UserException("exceptions.timetracking.error_timeTrack_before_users_entry");
   }
}

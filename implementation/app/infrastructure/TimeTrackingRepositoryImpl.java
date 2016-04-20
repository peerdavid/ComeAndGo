package infrastructure;

import business.UserException;
import com.avaje.ebean.Ebean;
import models.Break;
import models.TimeTrack;
import models.User;
import javassist.NotFoundException;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.*;

class TimeTrackingRepositoryImpl implements TimeTrackingRepository {

    @Override
    public List<TimeTrack> readTimeTracks(User user) {
        // user should not be null at that point

        List<TimeTrack> _timeTracks =
            Ebean.find(TimeTrack.class)
                .where().eq("_user_id", user.getId())
                .findList();

       if(_timeTracks == null) {
           return Collections.emptyList();
       }

        return _timeTracks;
    }


   /**
    * returns all timetracks from user, which are between from and to
    * @param user
    * @param from
    * @param to
    * @return
    * @throws NotFoundException
    */
    @Override
    public List<TimeTrack> readTimeTracks(User user, DateTime from, DateTime to) {
       if(from.isAfter(to))
          return Collections.emptyList();

        List<TimeTrack> _timeTracks =
            Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().ge("start", from)
            .where().le("end", to)
            .setOrderBy("start")
            .findList();

        // if we have the case that there was nothing found
       if(_timeTracks == null) {
           _timeTracks = Collections.emptyList();
       }

       // we also should include the active timeTrack:
       try {
          _timeTracks.add(getActiveTimeTrack(user));
       } catch (NotFoundException e) {
          // in this branch should be done anything
       }

       return _timeTracks;
    }

    @Override
    public TimeTrack readTimeTrack(int id) throws NotFoundException {
        TimeTrack wantedTimeTrack =
            Ebean.find(TimeTrack.class)
                .where().eq("id", id).findUnique();

        if (wantedTimeTrack != null) {
            return wantedTimeTrack;
        }

        // We should never return null
        throw new NotFoundException("exceptions.timetracking.could_not_find_timetrack");
    }

    /**
     * get all timeTracks which are overlapping to given timeTrack
     * @param user
     * @param timeTrack
     * @return list of timeTracks
     */
   @Override
   public List<TimeTrack> readTimeTracksOverlay(User user, TimeTrack timeTrack) {
      List<TimeTrack> wantedList =
          Ebean.find(TimeTrack.class)
          .where().eq("_user_id", user.getId())             // filter for only timeTracks from given user
          .disjunction()
            .conjunction()
                .where().ge("to", timeTrack.getFrom())
                .where().le("to", timeTrack.getTo())
            .endJunction()
            .conjunction()
                .where().ge("from", timeTrack.getFrom())
                .where().le("from", timeTrack.getTo())
            .endJunction()
          .endJunction()
          .findList();

      if(wantedList == null) {
         return Collections.emptyList();
      }

      return wantedList;
   }

    @Override
    public TimeTrack getActiveTimeTrack(User user) throws NotFoundException {
        TimeTrack actualTimeTrack = Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().isNull("end")
            .findUnique();
        if (actualTimeTrack != null) {
           return actualTimeTrack;
        }
        throw new NotFoundException("exceptions.timetracking.could_not_find_timetrack");
    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack) {
       Ebean.update(timeTrack);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {
       Ebean.delete(TimeTrack.class, timeTrack);
    }

    @Override
    public Break getActiveBreak(User user) throws NotFoundException {
        TimeTrack activeTimeTrack = getActiveTimeTrack(user);

        Break activeBreak =
            Ebean.find(Break.class)
                .where().eq("time_track_id", activeTimeTrack.getId())
                .where().isNull("end").findUnique();

        if (activeBreak != null) {
            return activeBreak;
        }
        throw new NotFoundException("exceptions.timetracking.could_not_find_break");
    }

    @Override
    public int createTimeTrack(TimeTrack timeTrack, User user) throws UserException {
        // first ensure that there is no TimeTrack already created for this user
        int rowCount = Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().isNull("end").findRowCount();
        if (rowCount != 0) {
            throw new UserException("exceptions.timetracking.user_timetrack_error");
        }

        Ebean.save(timeTrack);
        // refresh to get the auto_incremented id inside newTimeTrack
        Ebean.refresh(timeTrack);

        return timeTrack.getId();
    }

    @Override
    public void deleteBreak(Break actualBreak) {
       Ebean.delete(Break.class, actualBreak);
    }

    @Override
    public void updateBreak(Break actualBreak) {
       Ebean.update(actualBreak);
    }

    @Override
    public void startBreak(User user) throws NotFoundException {
        TimeTrack actualTimeTrack = getActiveTimeTrack(user);
        actualTimeTrack.addBreak(new Break(DateTime.now()));
        updateTimeTrack(actualTimeTrack);
    }

    @Override
    public void endBreak(Break actualBreak) throws TimeTrackException, UserException {
        actualBreak.setTo(DateTime.now());
        updateBreak(actualBreak);
    }

    @Override
    public void endBreak(User user) throws NotFoundException, TimeTrackException, UserException {
        Break actualBreak = getActiveBreak(user);
        endBreak(actualBreak);
    }

    @Override
    public void addTimeTrack(TimeTrack timeTrack) throws UserException {
            Ebean.save(timeTrack);
    }
}

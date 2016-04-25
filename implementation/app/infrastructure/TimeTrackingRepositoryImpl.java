package infrastructure;

import business.timetracking.TimeTrackException;
import business.usermanagement.UserException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import models.Break;
import models.TimeTrack;
import models.User;
import javassist.NotFoundException;
import org.joda.time.DateTime;

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
    * returns all timeTracks from user, which are between from and to
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
          _timeTracks.add(readActiveTimeTrack(user));
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
          .where().or(
                  Expr.and(
                          Expr.ge("end", timeTrack.getFrom()),
                          Expr.le("end", timeTrack.getTo())
                  ),
                  Expr.and(
                          Expr.ge("start", timeTrack.getFrom()),
                          Expr.le("start", timeTrack.getTo())
                  )
          )
          .findList();

      if(wantedList == null) {
         return Collections.emptyList();
      }

      return wantedList;
   }

    @Override
    public TimeTrack readActiveTimeTrack(User user) throws NotFoundException {
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
    public Break readActiveBreak(User user) throws NotFoundException {
        TimeTrack activeTimeTrack = readActiveTimeTrack(user);

        Break activeBreak =
            Ebean.find(Break.class)
                .where().eq("time_track_id", activeTimeTrack.getId())
                .where().isNull("end").findUnique();

        if (activeBreak != null) {
            return activeBreak;
        }
        throw new NotFoundException("exceptions.timetracking.could_not_find_break");
    }

    /**
     * creates a new timeTrack
     * @param timeTrack
     * @return timeTrack ID created by database after storing
     * @throws UserException
     */
    @Override
    public int createTimeTrack(TimeTrack timeTrack) throws UserException {
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
}

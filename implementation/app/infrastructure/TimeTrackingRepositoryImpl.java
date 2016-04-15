package infrastructure;

import com.avaje.ebean.Ebean;
import model.Break;
import model.TimeTrack;
import model.User;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

class TimeTrackingRepositoryImpl implements TimeTrackingRepository {

    private List<TimeTrack> _timeTracks = new ArrayList();

    public TimeTrackingRepositoryImpl() {
    }


    @Override
    public List<TimeTrack> readTimeTracks(User user) throws TimeTrackException{
       if(user == null) {
          throw new TimeTrackException("no user");
       }
       _timeTracks =
            Ebean.find(TimeTrack.class)
                .where().eq("_user_id", user.getId())
                .findList();

       if(_timeTracks == null) throw new TimeTrackException("timetracks not found");

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
    public List<TimeTrack> readTimeTracks(User user, DateTime from, DateTime to) throws TimeTrackException {
       if(from.isAfter(to))
          throw new TimeTrackException("from is after to");
       if(user == null)
          throw new TimeTrackException("no user given");

       _timeTracks =
            Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().ge("start", from)
            .where().le("end", to)
            .findList();

       if(_timeTracks == null) throw new TimeTrackException("list of timetracks not found");

       return _timeTracks;
    }

    @Override
    public TimeTrack readTimeTrack(int id) throws TimeTrackException {
        TimeTrack wantedTimeTrack =
            Ebean.find(TimeTrack.class)
                .where().eq("id", id).findUnique();

        if (wantedTimeTrack != null) {
            return wantedTimeTrack;
        }

        // We should never return null
        throw new TimeTrackException("Entity does not exist.");
    }

    @Override
    public TimeTrack getActiveTimeTrack(User user) throws NotFoundException {
        TimeTrack actualTimeTrack = Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().isNull("end")
            .findUnique();
        if (actualTimeTrack == null) {
           throw new NotFoundException("TimeTrack not found");
        }

        return actualTimeTrack;
    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack) {
       if(timeTrack == null) return;
       Ebean.update(timeTrack);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {
       if(timeTrack == null) return;
       Ebean.delete(TimeTrack.class, timeTrack);
    }

    @Override
    public Break getActiveBreak(User user) throws NotFoundException {
        TimeTrack activeTimeTrack = getActiveTimeTrack(user);

        Break activeBreak =
            Ebean.find(Break.class)
                .where().eq("time_track_id", activeTimeTrack.get_id())
                .where().isNull("end").findUnique();

        if (activeBreak == null) {
            throw new NotFoundException("break was not found");
        }
        return activeBreak;
    }

    @Override
    public int createTimeTrack(TimeTrack timeTrack, User user) throws TimeTrackException {
        // first ensure that there is no TimeTrack already created for this user
        int rowCount = Ebean.find(TimeTrack.class)
            .where().eq("_user_id", user.getId())
            .where().isNull("end").findRowCount();
        if (rowCount != 0) {
            throw new TimeTrackException("user already started work");
        }

        Ebean.save(timeTrack);
        // refresh to get the auto_incremented id inside newTimeTrack
        Ebean.refresh(timeTrack);

        return timeTrack.get_id();
    }

    @Override
    public void deleteBreak(Break actualBreak) {
       if(actualBreak == null) return;
       Ebean.delete(Break.class, actualBreak);
    }

    @Override
    public void updateBreak(Break actualBreak) {
       if(actualBreak == null) return;
       Ebean.update(actualBreak);
    }

    @Override
    public void startBreak(User user) throws TimeTrackException, NotFoundException {
        TimeTrack actualTimeTrack = getActiveTimeTrack(user);
        actualTimeTrack.addBreak(new Break(DateTime.now()));
        updateTimeTrack(actualTimeTrack);
    }

    @Override
    public void endBreak(Break actualBreak) throws TimeTrackException {
        actualBreak.setTo(DateTime.now());
        updateBreak(actualBreak);
    }

    @Override
    public void endBreak(User user) throws TimeTrackException, NotFoundException {
        Break actualBreak = getActiveBreak(user);
        endBreak(actualBreak);
    }
}

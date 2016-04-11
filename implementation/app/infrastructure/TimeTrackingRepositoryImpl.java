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

    public TimeTrackingRepositoryImpl(){ }


    @Override
    public List<TimeTrack> readTimeTracks(User user) {
       _timeTracks =
           Ebean.find(TimeTrack.class)
               .where().eq("_user_id", user.getId())
               .findList();

       return _timeTracks;
    }


    @Override
    public TimeTrack readTimeTrack(int id) throws NotFoundException {
        TimeTrack wantedTimeTrack =
            Ebean.find(TimeTrack.class)
                .where().eq("id", id).findUnique();

       if(wantedTimeTrack != null)  return wantedTimeTrack;

        // We should never return null
        throw new NotFoundException("Entity does not exist.");
    }

    @Override
    public TimeTrack getActiveTimeTrack(User user) throws NotFoundException {
       TimeTrack actualTimeTrack = Ebean.find(TimeTrack.class)
           .where().eq("_user_id", user.getId())
           .where().isNull("end")
           .findUnique();
       if(actualTimeTrack != null)
          return actualTimeTrack;

       throw new NotFoundException("not found");
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
           .where().eq("time_track_id", activeTimeTrack.get_id())
           .where().isNull("end").findUnique();

       if(activeBreak == null) {
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
       if(rowCount != 0) {
          throw new TimeTrackException("user already started work");
       }

       Ebean.save(timeTrack);
       // refresh to get the auto_incremented id inside newTimeTrack
       Ebean.refresh(timeTrack);

       return timeTrack.get_id();
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
   public void endBreak(Break actualBreak) {
      actualBreak.setTo(DateTime.now());
      updateBreak(actualBreak);
   }

   @Override
   public void endBreak(User user) throws NotFoundException {
      Break actualBreak = getActiveBreak(user);
      endBreak(actualBreak);
   }
}

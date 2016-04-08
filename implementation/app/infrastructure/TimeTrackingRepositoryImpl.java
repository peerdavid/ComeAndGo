package infrastructure;

import com.avaje.ebean.Ebean;
import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 21.03.16.
 * Note: We don't write unit tests for a repository. This should not be necessary
 * If we need it, we have made a design error.
 */
class TimeTrackingRepositoryImpl implements TimeTrackingRepository {

    private List<TimeTrack> _timeTracks = new ArrayList();

    public TimeTrackingRepositoryImpl(){ }


    @Override
    public List<TimeTrack> readTimeTracks(User user) {
       int id = user.getId();
       _timeTracks =
           Ebean.find(TimeTrack.class)
               .where().eq("user_id", id)
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
    public void updateTimeTrack(TimeTrack timeTrack) {
       Ebean.update(timeTrack);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {
       Ebean.delete(TimeTrack.class, timeTrack);
    }

    @Override
    public int createTimeTrack(TimeTrack timeTrack, User user)  {
       TimeTrack newTimeTrack = new TimeTrack(user);

       Ebean.save(newTimeTrack);
       // refresh to get the auto_incremented id inside newTimeTrack
       Ebean.refresh(newTimeTrack);

       return newTimeTrack.get_id();
    }
}

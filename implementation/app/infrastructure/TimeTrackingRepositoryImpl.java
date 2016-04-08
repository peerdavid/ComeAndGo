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
    private TimeTrack _timeTrack1 = new TimeTrack();
    private TimeTrack _timeTrack2 = new TimeTrack();

    public TimeTrackingRepositoryImpl(){
        _timeTracks.add(_timeTrack1);
        _timeTracks.add(_timeTrack2);
    }


    @Override
    public List<TimeTrack> readTimeTracks(User user) throws NotFoundException {
       int id = user.getId();
       _timeTracks =
           Ebean.find(TimeTrack.class)
               .where().eq("user_id", id)
               .findList();

       if(_timeTracks == null) throw new NotFoundException("Not found timetracks from user");

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
    public int createTimeTrack(TimeTrack timeTrack, User user) throws NotFoundException {
       TimeTrack newTimeTrack = new TimeTrack(user);

       Ebean.save(newTimeTrack);
       // refresh to get the auto_incremented id inside newTimeTrack
       Ebean.refresh(newTimeTrack);

       return newTimeTrack.get_id();
    }
}

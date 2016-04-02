package infrastructure;

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
    public List<TimeTrack> readTimeTracks(User user) {
        return _timeTracks;
    }


    @Override
    public TimeTrack readTimeTrack(int id) throws NotFoundException {
        if(_timeTracks.stream().anyMatch(timeTrack -> timeTrack.get_id() == id)){
            return (TimeTrack) _timeTracks.stream()
                    .filter(timeTrack -> timeTrack.get_id() == id)
                    .limit(1)
                    .toArray()[0];
        }

        // We should never return null
        throw new NotFoundException("Entity does not exist.");
    }


    @Override
    public void updateTimeTrack(TimeTrack timeTrack) {

    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {

    }

    @Override
    public int createTimeTrack(TimeTrack timeTrack) {
        _timeTracks.add(timeTrack);
        return -1; // ToDo: Return id of time track
    }
}

package business.reporting;

import models.TimeOff;
import models.TimeTrack;
import models.User;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAggreement {

    double getSalaryOfUser(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs);
}

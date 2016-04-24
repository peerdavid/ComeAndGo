package business.timetracking;

import business.TimeTrackException;
import models.TimeOff;

/**
 * Created by paz on 24.04.16.
 */
public interface TimeOffService {

    void takeSickLeave(TimeOff sickLeave) throws Exception;

    void takeBusinessTrip(TimeOff trip) throws Exception;

    void  requestTimeOff(TimeOff timeoff) throws Exception;

    void  acceptTimeOff(TimeOff timeoff) throws Exception;

    void  rejectTimeOff(TimeOff timeoff) throws Exception;


}

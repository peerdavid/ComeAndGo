package business.timetracking;

import business.UseCases;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTracking extends UseCases {
    void come(int userId) throws Exception;

    void go(int userId) throws Exception;

    void startBreak(int userId) throws Exception;

    void endBreak(int userId) throws Exception;

    TimeTrackState readState(int userId) throws Exception;

    TimeTrack readTimeTrackById(int id) throws Exception;

    List<TimeTrack> readTimeTracks(int userId) throws Exception;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception;

    void createTimeTrack(int userId, DateTime from, DateTime to) throws Exception;

    void deleteTimeTrack(TimeTrack timeTrack) throws Exception;

    void updateTimeTrack(TimeTrack timeTrack) throws Exception;

    void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  acceptHoliday(int timeOffId, int bossId) throws Exception;

    void  rejectHoliday(int timeOffId, int bossId) throws Exception;
}

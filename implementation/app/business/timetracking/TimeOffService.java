package business.timetracking;

import models.TimeOff;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by paz on 24.04.16.
 */
interface TimeOffService {

    void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void takeParentalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  requestEducationalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception;

    void  acceptHoliday(int timeOffId, int bossId) throws Exception;

    void  rejectHoliday(int timeOffId, int bossId) throws Exception;

    void  acceptSpecialHoliday(int timeOffId, int bossId) throws Exception;

    void  rejectSpecialHoliday(int timeOffId, int bossId) throws Exception;

    void  acceptEducationalLeave(int timeOffId, int bossId) throws Exception;

    void  rejectEducationalLeave(int timeOffId, int bossId) throws Exception;

    TimeOff readTimeOffById(int timeOffId) throws Exception;

    List<TimeOff> readTimeOffs(int userId) throws Exception;

    void deleteTimeTrack(int userId, int id) throws TimeTrackException, Exception;
}

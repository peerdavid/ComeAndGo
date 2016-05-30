package business.timetracking;

import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Payout;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
class TimeTrackingFacade implements TimeTracking {


    private TimeTrackingService _timeTrackingService;
    private TimeOffService _timeOffService;
    private PayoutService _payoutService;

    @Inject
    public TimeTrackingFacade(TimeTrackingService timeTrackingService, TimeOffService timeOffService, PayoutService payoutService) {
        _timeTrackingService = timeTrackingService;
        _timeOffService = timeOffService;
        _payoutService = payoutService;
    }

    @Override
    public void come(int userId) throws Exception {
        _timeTrackingService.come(userId);
    }


    @Override
    public void go(int userId) throws Exception {
        _timeTrackingService.go(userId);
    }

    @Override
    public void startBreak(int userId) throws Exception {
        _timeTrackingService.createBreak(userId);
    }


    @Override
    public void endBreak(int userId) throws Exception {
        _timeTrackingService.endBreak(userId);
    }


    @Override
    public TimeTrackState readState(int userId) throws Exception {
        TimeTrackState result = TimeTrackState.INACTIVE;

        if(_timeTrackingService.isActive(userId)) {
            result = TimeTrackState.ACTIVE;
        }

        if(_timeTrackingService.takesBreak(userId)) {
            result = TimeTrackState.PAUSE;
        }

        return result;
    }

    @Override
    public TimeTrack readTimeTrackById(int id) throws Exception {
        return _timeTrackingService.readTimeTrackById(id);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws Exception {
        return _timeTrackingService.readTimeTracks(userId);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception {
        return _timeTrackingService.readTimeTracks(userId, from, to);
    }


    @Override
    public void createTimeTrack(int userId, DateTime from, DateTime to,int currentUserId, String message) throws Exception {
        _timeTrackingService.createTimeTrack(userId, from, to, currentUserId);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack, int currentUserId, String message) throws Exception {
        _timeTrackingService.deleteTimeTrack(timeTrack, currentUserId);
    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack, int currentUserId, String message) throws Exception {
        _timeTrackingService.updateTimeTrack(timeTrack, currentUserId);
    }

    @Override
    public void deleteTimeOff(int userId, int id) throws Exception {
        _timeOffService.deleteTimeOff(userId, id);
    }

    @Override
    public void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeSickLeave(userId, from, to, comment);
    }

    @Override
    public void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeBusinessTrip(userId, from, to, comment);
    }

    @Override
    public void requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestHoliday(userId, from, to, comment);
    }

    @Override
    public void requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestSpecialHoliday(userId, from, to, comment);
    }

    @Override
    public void acceptHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptHoliday(timeOffId, bossId);
    }

    @Override
    public void rejectHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectHoliday(timeOffId, bossId);
    }

    @Override
    public void takeParentalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeParentalLeave(userId, from, to, comment);
    }

    @Override
    public void requestEducationalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestEducationalLeave(userId, from, to, comment);
    }

    @Override
    public void acceptSpecialHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptSpecialHoliday(timeOffId, bossId);

    }

    @Override
    public void rejectSpecialHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectSpecialHoliday(timeOffId, bossId);
    }

    @Override
    public void acceptEducationalLeave(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptEducationalLeave(timeOffId, bossId);
    }

    @Override
    public void rejectEducationalLeave(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectEducationalLeave(timeOffId, bossId);
    }

    @Override
    public TimeOff readTimeOffById(int timeOffId) throws Exception {
        return _timeOffService.readTimeOffById(timeOffId);
    }

    @Override
    public List<TimeOff> readTimeOffs(int userId) throws Exception {
        return _timeOffService.readTimeOffs(userId);
    }

    @Override
    public void requestOvertimePayout(int userId, int numberOfHours, String comment) throws Exception {
        _payoutService.requestOvertimePayout(userId, numberOfHours, comment);
    }

    @Override
    public void requestHolidayPayout(int userId, int numberOfDays, String comment) throws Exception {
        _payoutService.requestHolidayPayout(userId, numberOfDays, comment);
    }

    @Override
    public void acceptHolidayPayout(int payoutId, int bossId) throws Exception {
        _payoutService.acceptHolidayPayout(payoutId, bossId);
    }

    @Override
    public void rejectHolidayPayout(int payoutId, int bossId) throws Exception {
        _payoutService.rejectHolidayPayout(payoutId, bossId);
    }

    @Override
    public void acceptOvertimePayout(int payoutId, int bossId) throws Exception {
        _payoutService.acceptOvertimePayout(payoutId, bossId);
    }

    @Override
    public void rejectOvertimePayout(int payoutId, int bossId) throws Exception {
        _payoutService.rejectOvertimePayout(payoutId, bossId);
    }

    @Override
    public void deletePayoutRequest(int payoutId) throws Exception {
        _payoutService.deletePayout(payoutId);
    }

    @Override
    public List<Payout> readPayoutsFromUser(int userId) throws Exception {
        return _payoutService.readPayoutsFromUser(userId);
    }

    @Override
    public List<Payout> readAcceptedPayoutsFromUser(int userId) throws Exception {
        return _payoutService.readAcceptedPayoutsFromUser(userId);
    }

    @Override
    public Payout readPayout(int payoutId) throws Exception {
        return _payoutService.readPayout(payoutId);
    }

    @Override
    public void updatePayoutRequest(Payout payout) throws Exception {
        _payoutService.updatePayoutRequest(payout);
    }

    @Override
    public void createBankHoliday(int userId, DateTime from, DateTime to, String nameOfBankHoliday) throws Exception {
        _timeOffService.createBankHoliday(userId, from, to, nameOfBankHoliday);
    }
}

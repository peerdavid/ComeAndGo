package business.reporting;

import business.notification.InternalNotificationSender;
import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Stefan on 09.05.2016.
 */
public class ReportingServiceImplTest {

    InternalNotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    InternalUserManagement _internalUserManagement;
    User _testUser;
    ReportingService _reporting;
    CollectiveAgreement _collectiveAgreement;
    InternalTimeTracking _internalTimeTrack;
    DateTime _now;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 1200);

        _notificationSenderMock = mock(InternalNotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _internalUserManagement = mock(InternalUserManagement.class);
        _internalTimeTrack = mock(InternalTimeTracking.class);
        _collectiveAgreement = mock(CollectiveAgreement.class);
        _reporting = new ReportingServiceImpl(_internalUserManagement, _collectiveAgreement, _internalTimeTrack);

        _now = DateTime.now();
    }

    @Test
    public void getHoursWorked_WithNoTimeTracks_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class), any(DateTime.class), any(DateTime.class))).thenReturn(Collections.emptyList());

        int userId = 8;
        double expected = 0;

        // Call
        double actual = _reporting.readHoursWorked(userId, _now);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void getHoursWorked_WithBreak_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        TimeTrack t = Mockito.mock(TimeTrack.class);
        when(t.getFrom()).thenReturn(DateTime.now().minusHours(6));
        when(t.getTo()).thenReturn(DateTime.now());

        List<TimeTrack> timeTracks = new ArrayList<>();
        timeTracks.add(t);

        Break b = Mockito.mock(Break.class);
        when(b.getFrom()).thenReturn(DateTime.now().minusHours(4));
        when(b.getTo()).thenReturn(DateTime.now());

        List<Break> breaks = new ArrayList<>();
        breaks.add(b);
        when(t.getBreaks()).thenReturn(breaks);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTracks);

        int userId = 8;
        double expected = 2.0;

        // Call
        double actual = _reporting.readHoursWorked(userId, _now);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.05);
    }

    @Test
    public void getHoursWorked_WithMultipleTimeTracksAndBreaks_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        TimeTrack t1 = Mockito.mock(TimeTrack.class);
        when(t1.getFrom()).thenReturn(DateTime.now().minusHours(7));
        when(t1.getTo()).thenReturn(DateTime.now().minusHours(4));
        TimeTrack t2 = Mockito.mock(TimeTrack.class);
        when(t2.getFrom()).thenReturn(DateTime.now().minusHours(3));
        when(t2.getTo()).thenReturn(DateTime.now());

        List<TimeTrack> timeTracks = new ArrayList<>();
        timeTracks.add(t1);
        timeTracks.add(t2);

        Break b1 = Mockito.mock(Break.class);
        when(b1.getFrom()).thenReturn(DateTime.now().minusHours(6));
        when(b1.getTo()).thenReturn(DateTime.now().minusHours(5));

        Break b2 = Mockito.mock(Break.class);
        when(b2.getFrom()).thenReturn(DateTime.now().minusHours(2));
        when(b2.getTo()).thenReturn(DateTime.now().minusHours(1));

        List<Break> breaks = new ArrayList<>();
        breaks.add(b1);
        breaks.add(b2);
        when(t1.getBreaks()).thenReturn(breaks);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTracks);

        int userId = 8;
        double expected = 4.0;

        // Call
        double actual = _reporting.readHoursWorked(userId, _now);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.05);
    }

    @Test(expected = UserException.class)
    public void getHoursWorked_ForUnregisteredUser_ShouldThrowException() throws Exception {
        // Prepare
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenThrow(UserException.class);
        int userId = 8;

        // Call
        _reporting.readHoursWorked(userId, _now);
    }

    @Test
    public void getHoursWorked_OverPreviousMidnight_ShouldSucceedAndCallRepository() throws Exception {
        // init
        List<TimeTrack> toReturn = new ArrayList<>();

        // generate first timeTrack with break over midnight: working from 19.00 - 02.00
        // also generate a break from 23.30 - 00.30
        // --> effective working time here --> 1,5h
        List<Break> breakList = new ArrayList<>();
        DateTime midNight = DateTimeUtils.startOfDay(_now);
        breakList.add(new Break(midNight.minusMinutes(30), midNight.plusMinutes(30)));
        DateTime startOfNightWork = midNight.minusHours(5);
        DateTime endOfNightWork = midNight.plusHours(2);
        TimeTrack nightWork = new TimeTrack(_testUser, startOfNightWork, endOfNightWork, breakList);
        toReturn.add(nightWork);

        // generate second timeTrack without break
        // effective work time here --> 7h
        TimeTrack dayWork = new TimeTrack(_testUser, midNight.plusHours(8), midNight.plusHours(15), null);
        toReturn.add(dayWork);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(toReturn);
        double expectedHours = 8.5;

        // execute test
        double resultHours = _reporting.readHoursWorked(8, _now);

        // verify results
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expectedHours, resultHours, 0.01);
    }

    @Test
    public void getHoursWorked_OverNextMidnight_ShouldSucceedAndCallRepository() throws Exception {
        // init
        List<TimeTrack> toReturn = new ArrayList<>();

        // generate first timeTrack with break over midnight: working from 19.00 - 02.00
        // also generate a break from 23.30 - 00.30
        // --> effective working time here --> 4,5h
        List<Break> breakList = new ArrayList<>();
        DateTime midNight = DateTimeUtils.endOfDay(_now);
        breakList.add(new Break(midNight.minusMinutes(30), midNight.plusMinutes(30)));
        DateTime startOfNightWork = midNight.minusHours(5);
        DateTime endOfNightWork = midNight.plusHours(2);
        TimeTrack nightWork = new TimeTrack(_testUser, startOfNightWork, endOfNightWork, breakList);
        toReturn.add(nightWork);

        // generate second timeTrack without break
        // effective work time here --> 5h
        List<Break> breaksOverDay = new ArrayList<>();
        breaksOverDay.add(new Break(midNight.minusHours(12), midNight.minusHours(11)));
        TimeTrack dayWork = new TimeTrack(_testUser, midNight.minusHours(16), midNight.minusHours(10), breaksOverDay);
        toReturn.add(dayWork);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(toReturn);
        double expectedHours = 9.5;

        // execute test
        double resultHours = _reporting.readHoursWorked(8, _now);

        // verify results
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expectedHours, resultHours, 0.001);
    }

    @Test
    public void getHoursWorkedProgress_WithNoTimeTracks_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(Collections.emptyList());

        _testUser.setHoursPerDay(2.0);

        int userId = 8;
        double expected = 0;

        // Call
        double actual = _reporting.readHoursWorkedProgress(userId);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.01);
    }



    @Test
    public void getHoursWorkedProgress_WithBreak_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        TimeTrack t = Mockito.mock(TimeTrack.class);
        when(t.getFrom()).thenReturn(DateTime.now().minusHours(6));
        when(t.getTo()).thenReturn(DateTime.now());

        List<TimeTrack> timeTracks = new ArrayList<>();
        timeTracks.add(t);

        Break b = Mockito.mock(Break.class);
        when(b.getFrom()).thenReturn(DateTime.now().minusHours(4));
        when(b.getTo()).thenReturn(DateTime.now());

        List<Break> breaks = new ArrayList<>();
        breaks.add(b);
        when(t.getBreaks()).thenReturn(breaks);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTracks);

        _testUser.setHoursPerDay(4.0);

        int userId = 8;
        double expected = 0.5;

        // Call
        double actual = _reporting.readHoursWorkedProgress(userId);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.01);
    }

    @Test
    public void getHoursWorkedProgress_WithMoreThanHundredPercent_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        TimeTrack t = Mockito.mock(TimeTrack.class);
        when(t.getFrom()).thenReturn(DateTime.now().minusHours(6));
        when(t.getTo()).thenReturn(DateTime.now());

        List<TimeTrack> timeTracks = new ArrayList<>();
        timeTracks.add(t);

        when(t.getBreaks()).thenReturn(Collections.emptyList());

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTracks);

        _testUser.setHoursPerDay(4.0);

        int userId = 8;
        double expected = 1.0;

        // Call
        double actual = _reporting.readHoursWorkedProgress(userId);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.01);
    }

    @Test
    public void getHoursWorked_ForTwoDaysWithBreakOverMidnight_ShouldSucceedAndCallRepository() throws Exception {
        TimeTrack toTest = mock(TimeTrack.class);
        List<Break> breakList = new ArrayList<>();
        // break start at BIG BANG (23.40)
        DateTime breakStart = DateTimeUtils.endOfDay(DateTimeUtils.BIG_BANG).minusMinutes(20);
        // break end at one day after BIG BANG (00.15)
        DateTime breakEnd = DateTimeUtils.endOfDay(DateTimeUtils.BIG_BANG).plusMinutes(15);
        breakList.add(new Break(breakStart, breakEnd));
        // timeTrack starts yesterday at 20.00 and ends today at 06.00
        when(toTest.getBreaks()).thenReturn(breakList);
        when(toTest.getFrom()).thenReturn(DateTimeUtils.startOfDay(DateTime.now()).minusHours(4));
        when(toTest.getTo()).thenReturn(DateTimeUtils.startOfDay(DateTime.now()).plusHours(6));

        List<TimeTrack> timeTrackList = new ArrayList<>();
        timeTrackList.add(toTest);

        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTrackList);

        double hoursWorkedDayBefore = _reporting.readHoursWorked(8, DateTime.now().minusDays(1));
        double hoursWorkedActualDay = _reporting.readHoursWorked(8, DateTime.now());

        Assert.assertEquals(hoursWorkedDayBefore, 3.67, 0.1);
        Assert.assertEquals(hoursWorkedActualDay, 5.75, 0.1);
    }

    @Test
    public void getHoursWorkedProgress_WithMultipleTimeTracksAndBreaks_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        TimeTrack t1 = Mockito.mock(TimeTrack.class);
        when(t1.getFrom()).thenReturn(DateTime.now().minusHours(7));
        when(t1.getTo()).thenReturn(DateTime.now().minusHours(4));
        TimeTrack t2 = Mockito.mock(TimeTrack.class);
        when(t2.getFrom()).thenReturn(DateTime.now().minusHours(3));
        when(t2.getTo()).thenReturn(DateTime.now());

        List<TimeTrack> timeTracks = new ArrayList<>();
        timeTracks.add(t1);
        timeTracks.add(t2);

        Break b1 = Mockito.mock(Break.class);
        when(b1.getFrom()).thenReturn(DateTime.now().minusHours(6));
        when(b1.getTo()).thenReturn(DateTime.now().minusHours(5));

        Break b2 = Mockito.mock(Break.class);
        when(b2.getFrom()).thenReturn(DateTime.now().minusHours(2));
        when(b2.getTo()).thenReturn(DateTime.now().minusHours(1));

        List<Break> breaks = new ArrayList<>();
        breaks.add(b1);
        breaks.add(b2);
        when(t1.getBreaks()).thenReturn(breaks);

        _testUser.setHoursPerDay(6.0);

        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(timeTracks);

        int userId = 8;
        double expected = 0.66;

        // Call
        double actual = _reporting.readHoursWorkedProgress(userId);

        // Validate
        Mockito.verify(_internalTimeTrack, times(1)).readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class));
        Assert.assertEquals(expected, actual, 0.01);
    }

    @Test(expected = UserException.class)
    public void getHoursWorkedProgress_ForUnregisteredUser_ShouldThrowException() throws Exception {
        // Prepare
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenThrow(UserException.class);

        int userId = 8;

        // Call
        _reporting.readHoursWorked(userId, _now);
    }

    @Test
    public void getHoursOfBreaks_ForGivenDateTime_ShouldSucceed() throws Exception {
        DateTime startOfDay = DateTimeUtils.startOfDay(_now);
        DateTime endOfDay = DateTimeUtils.endOfDay(_now);

        DateTime startOfBigBang = DateTimeUtils.endOfDay(DateTimeUtils.BIG_BANG).plusMillis(1);

        // this test contains 3 timeTracks with each one break (night work has break over midnight)
        //  NOTE: dates of breaks are senseless, since we only store times in our database
        //  we test also that breaks with no valid date should work exactly as it should...

        // timeTrack from 8pm the previous day until 4am actual day
        // break from 23.47 - 00.09
        // resulting breaks --> 9min
        Break firstMidnightBreak = new Break(startOfBigBang.minusMinutes(13), startOfBigBang.plusMinutes(9));
        List<Break> firstBreaksOverMidnight = new ArrayList<>();
        firstBreaksOverMidnight.add(firstMidnightBreak);
        TimeTrack _timeTrackOverFirstNight = new TimeTrack(_testUser, startOfDay.minusHours(4), startOfDay.plusHours(4), firstBreaksOverMidnight);

        // timeTrack from 8am to 4pm
        // break from 12.00 - 12.30
        // resulting breaks --> 30min
        Break breakOverDay = new Break(startOfBigBang.plusHours(12), startOfBigBang.plusHours(12).plusMinutes(30));
        List<Break> breaksOverDay = new ArrayList<>();
        breaksOverDay.add(breakOverDay);
        TimeTrack _timeTrackOverDay = new TimeTrack(_testUser, startOfDay.plusHours(8), startOfDay.plusHours(16), breaksOverDay);

        // timeTrack from 8pm until 0.40am
        // break from 23.43 - 00.00
        // resulting break --> 17min
        Break secondMidnightBreak = new Break(startOfBigBang.minusMinutes(17), startOfBigBang);
        List<Break> secondBreaksOverMidnight = new ArrayList<>();
        secondBreaksOverMidnight.add(secondMidnightBreak);
        TimeTrack _timeTrackOverSecondMidnight = new TimeTrack(_testUser, endOfDay.minusHours(4), endOfDay.plusMinutes(40), secondBreaksOverMidnight);

        List<TimeTrack> listToReturn = new ArrayList<>();
        listToReturn.add(_timeTrackOverFirstNight);
        listToReturn.add(_timeTrackOverDay);
        listToReturn.add(_timeTrackOverSecondMidnight);
        when(_internalTimeTrack.readTimeTracks(any(Integer.class), any(DateTime.class), any(DateTime.class))).thenReturn(listToReturn);

        // we have 9 + 30 + 17 min of breaks = 56min
        double hoursOfBreakExpected = 56f / 60f;
        double resultingHoursOfBreak = _reporting.readHoursOfBreak(8, _now);
        Assert.assertEquals(hoursOfBreakExpected, resultingHoursOfBreak, 0.05);
    }

}

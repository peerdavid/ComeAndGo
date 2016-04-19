package business.timetracking;

import business.UserException;
import business.notification.NotificationSender;
import business.usermanagement.SecurityRole;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
import javassist.NotFoundException;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Stefan on 19.04.2016.
 */
public class TimeTrackingValidationImplTest {
    NotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    UserRepository _userRepository;
    TimeTrackingService _timeTrackService;
    User _testUser;
    Break _testBreak;
    TimeTrackingValidation _validation;
    List<TimeTrack> _testList;
    DateTime _MIDNIGHT;
    DateTime _MIDDAY;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testuser", "asdfasdf", SecurityRole.ROLE_USER, "Hans", "Wurst", "hans@wurst.at", true, "testBoss");
        _testBreak = new Break(DateTime.now());

        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(UserRepository.class);

        _timeTrackService = new TimeTrackingServiceImpl(_timeTrackingRepository, _notificationSenderMock, _userRepository);
        _validation = new TimeTrackingValidationImpl(_timeTrackingRepository);

        _testList = new ArrayList();
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(8), null);
        timeTrack.setId(1);
        _testList.add(timeTrack);

        _MIDNIGHT  = new DateTime(2016, 5, 3, 0, 0, 0);
        _MIDDAY = _MIDNIGHT.plusHours(12);
    }

    /*
     *
     * THIS SECTION TESTS ALL ABOUT INSERTING A NEW TIMETRACK
     *
     */
    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WithNonEmptyOverlayResult_ShouldThrowExceptionAndCallRepo() throws UserException {
        // init
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now().minusHours(5), DateTime.now().plusHours(1), null);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);

        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WithEmptyOverlayListAndTwoValidBreaks_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.plusMinutes(10), _MIDNIGHT.plusMinutes(20));
        Break secondBreak = new Break(_MIDNIGHT.plusHours(1), _MIDNIGHT.plusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.plusMinutes(1), _MIDNIGHT.plusHours(10), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WithEmptyOverlayListAndClashingBreaks_ShouldThrowUserExceptionCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.plusMinutes(10), _MIDNIGHT.plusMinutes(30));
        Break secondBreak = new Break(_MIDNIGHT.plusMinutes(28), _MIDNIGHT.plusHours(1));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.plusMinutes(5), _MIDNIGHT.plusHours(8), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakBeforeMidnightAndAfterMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.minusHours(3), _MIDNIGHT.minusHours(2));
        Break secondBreak = new Break(_MIDNIGHT.plusHours(1), _MIDNIGHT.plusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(5), _MIDNIGHT.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakAroundMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.minusMinutes(10), _MIDNIGHT.plusMinutes(10));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(5), _MIDNIGHT.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        //
        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakBeforeMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.minusHours(3), _MIDNIGHT.minusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(5), _MIDNIGHT.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakAfterMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDNIGHT.plusHours(1), _MIDNIGHT.plusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(5), _MIDNIGHT.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingOnDayAndTakingNonClashingBreaks_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDDAY.minusHours(3), _MIDDAY.minusHours(2));
        Break secondBreak = new Break(_MIDDAY.plusMinutes(10), _MIDDAY.plusMinutes(30));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDDAY.minusHours(5), _MIDDAY.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WorkingOnDayAndTakingClashingBreaks_ShouldThrowUserExceptionAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(_MIDDAY.minusHours(1), _MIDDAY.minusMinutes(30));
        Break secondBreak = new Break(_MIDDAY.minusMinutes(31), _MIDDAY.plusMinutes(10));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDDAY.minusHours(5), _MIDDAY.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WorkingOnDayAndTakingBreaksBeforeTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
        // init

        Break firstBreak = new Break(_MIDDAY.plusMinutes(20), _MIDDAY.plusMinutes(30));
        Break secondBreak = new Break(_MIDDAY.minusMinutes(31), _MIDDAY.plusMinutes(10));
        Break thirdBreak = new Break(_MIDDAY.minusHours(6), _MIDDAY.minusHours(4)); // this one is outside timetrack
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        breakList.add(thirdBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDDAY.minusHours(5), _MIDDAY.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WorkingOnDayAndTakingBreaksAfterTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
        // initialize
        Break firstBreak = new Break(_MIDDAY.plusMinutes(20), _MIDDAY.plusMinutes(30));
        Break secondBreak = new Break(_MIDDAY.plusHours(2), _MIDDAY.plusHours(4));  // this one is outside timetrack
        Break thirdBreak = new Break(_MIDDAY.minusMinutes(31), _MIDDAY.plusMinutes(10));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        breakList.add(thirdBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDDAY.minusHours(5), _MIDDAY.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WorkingInNightAndTakingBreaksBeforeTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
        // initialize

        Break firstBreak = new Break(_MIDNIGHT.plusMinutes(20), _MIDNIGHT.plusMinutes(30));
        Break secondBreak = new Break(_MIDNIGHT.minusHours(6), _MIDNIGHT.minusHours(5));  // this one is outside timetrack
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(5), _MIDNIGHT.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WorkingInNightAndTakingBreaksAfterTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
        // initialize

        Break firstBreak = new Break(_MIDNIGHT.plusMinutes(20), _MIDNIGHT.plusMinutes(30));
        Break secondBreak = new Break(_MIDNIGHT.plusHours(4), _MIDNIGHT.plusHours(5));  // this one is outside timetrack
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);

        TimeTrack timeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(6), _MIDNIGHT.plusHours(4), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // start the test method
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

}

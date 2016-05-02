package business.timetracking;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import business.notification.NotificationSender;
import business.usermanagement.SecurityRole;
import infrastructure.TimeOffRepository;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
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
    InternalUserManagement _userRepository;
    TimeTrackingService _timeTrackService;
    TimeOffRepository _timeOffRepository;
    User _testUser;
    TimeTrack _testTimeTrack;
    Break _testBreak;
    TimeTrackingValidation _validation;
    TimeOffValidation _timeOffValidation;
    List<TimeTrack> _testList;
    DateTime _MIDNIGHT;
    DateTime _MIDDAY;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testuser", "asdfasdf", SecurityRole.ROLE_USER, "Hans", "Wurst", "hans@wurst.at", true, null, 1200);
        _testBreak = new Break(DateTime.now());

        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(InternalUserManagement.class);
        _validation = new TimeTrackingValidationImpl(_timeTrackingRepository);
        _timeOffRepository = mock(TimeOffRepository.class);
        _timeOffValidation = new TimeOffValidationImpl(_timeOffRepository);

        _timeTrackService = new TimeTrackingServiceImpl(_timeTrackingRepository, _validation, _timeOffValidation, _notificationSenderMock, _userRepository);

        _MIDNIGHT  = new DateTime(2016, 5, 3, 0, 0, 0);
        _MIDDAY = _MIDNIGHT.plusHours(12);

        _testList = new ArrayList();
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(8), null);
        timeTrack.setId(1);
        _testList.add(timeTrack);

        _testTimeTrack = timeTrack;
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
    public void validateTimeTrackInsert_WithBreakListEqualsNull_ShouldSucceedAndCallRepo() throws UserException {
        TimeTrack _testTimeTrack = new TimeTrack(_testUser, _MIDDAY, _MIDDAY.plusHours(5), null);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        _validation.validateTimeTrackInsert(_testTimeTrack);
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
    public void validateTimeTrackInsert_WorkingOnDayAndTakingBreakAtStartOfTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
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
    public void validateTimeTrackInsert_WorkingOnDayAndTakingBreaksAtEndOfrTimeTrack_ShouldThrowUserExceptionAndCallRepo() throws UserException {
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

    @Test
    public void validateTimeTrackInsert_WithAllMixedBreakTypesAtNightButNotClashing_ShouldSucceed() throws UserException {
        Break firstBreak = new Break(_MIDNIGHT.minusHours(3), _MIDNIGHT.minusHours(2));         // 21.00 - 22.00
        Break secondBreak = new Break(_MIDNIGHT.minusMinutes(90), _MIDNIGHT.minusMinutes(60));  // 22.30 - 23.00
        Break thirdBreak = new Break(_MIDNIGHT.minusMinutes(50), _MIDNIGHT.minusMinutes(30));   // 23.10 - 23.30
        Break fourthBreak = new Break(_MIDNIGHT.minusMinutes(5), _MIDNIGHT.plusMinutes(5));     // 23.55 - 00.05
        Break fifthBreak = new Break(_MIDNIGHT.plusHours(1), _MIDNIGHT.plusHours(2));           // 01.00 - 02.00
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        breakList.add(thirdBreak);
        breakList.add(fourthBreak);
        breakList.add(fifthBreak);

        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());
        TimeTrack _testTimeTrack = new TimeTrack(_testUser, _MIDNIGHT.minusHours(4), _MIDNIGHT.plusHours(3), breakList);

        _validation.validateTimeTrackInsert(_testTimeTrack);
        //Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackUpdate_WhenNoBreaksAreGivenAndNoClashingTimeTracks_ShouldSucceedAndCallRepo() throws UserException {
        TimeTrack _testTimeTrack = new TimeTrack(_testUser, _MIDDAY.minusHours(1), _MIDDAY.plusHours(4), null);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // start test
        _validation.validateTimeTrackUpdate(_testTimeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackUpdate_WithOneSingleOverlayTimeTrackWhichIsSameOne_ShouldSucceed() throws UserException {
        // _testList contains only one timeTrack which is _testTimeTrack
        // timeTracks are allowed to overlay itself before changing
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);

        _validation.validateTimeTrackUpdate(_testTimeTrack);
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackUpdate_WithTwoOverlayTimeTracks_ShouldThrowException() throws UserException {
        // give _testList a second element
        TimeTrack secondTimeTrack = new TimeTrack(_testUser, _MIDDAY, _MIDDAY.plusHours(1), null);
        secondTimeTrack.setId(2);
        _testList.add(secondTimeTrack);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);

        _validation.validateTimeTrackUpdate(_testTimeTrack);
    }

}

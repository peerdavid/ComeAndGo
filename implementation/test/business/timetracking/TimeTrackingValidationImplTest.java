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
    DateTime midNight;


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

        midNight  = new DateTime(2016, 5, 3, 0, 0, 0);
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
        Break firstBreak = new Break(midNight.plusMinutes(10), midNight.plusMinutes(20));
        Break secondBreak = new Break(midNight.plusHours(1), midNight.plusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, midNight.plusMinutes(1), midNight.plusHours(10), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test(expected = UserException.class)
    public void validateTimeTrackInsert_WithEmptyOverlayListAndClashingBreaks_ShouldThrowUserExceptionCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(midNight.plusMinutes(10), midNight.plusMinutes(30));
        Break secondBreak = new Break(midNight.plusMinutes(28), midNight.plusHours(1));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, midNight.plusMinutes(5), midNight.plusHours(8), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakBeforeMidnightAndAfterMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(midNight.minusHours(3), midNight.minusHours(2));
        Break secondBreak = new Break(midNight.plusHours(1), midNight.plusHours(2));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        breakList.add(secondBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, midNight.minusHours(5), midNight.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WorkingAroundMidnightAndTakingBreakAroundMidnight_ShouldSucceedAndCallRepo() throws UserException {
        // init
        Break firstBreak = new Break(midNight.minusMinutes(10), midNight.plusMinutes(10));
        List<Break> breakList = new ArrayList<>();
        breakList.add(firstBreak);
        TimeTrack timeTrack = new TimeTrack(_testUser, midNight.minusHours(5), midNight.plusHours(3), breakList);
        timeTrack.setUser(_testUser);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());

        // execute test
        _validation.validateTimeTrackInsert(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }


}

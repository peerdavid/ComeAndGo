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
    }

    @Test
    public void validateTimeTrackInsert_WithNonEmpyOverlayResult_ShouldReturnFalseAndCallRepo() throws UserException {
        // init
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now().minusHours(5), DateTime.now().plusHours(1), null);

        boolean expected = false;
        boolean result = _validation.validateTimeTrackInsert(timeTrack);
        Assert.assertEquals(expected, result);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateTimeTrackInsert_WithEmptyOverlayList_ShouldReturnTrueAndCallRepo() throws UserException {
        // init
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now().minusHours(5), DateTime.now().plusHours(1), null);

        boolean expected = true;
        boolean result = _validation.validateTimeTrackInsert(timeTrack);
        Assert.assertEquals(expected, result);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void validateUpdateTimeTrack_WhichIsTheActiveOne_ShouldReturnTrueAndCallRepo() throws UserException, NotFoundException {
        // init
        TimeTrack actualTimeTrack = new TimeTrack(_testUser);
        actualTimeTrack.setId(5);
        actualTimeTrack.setUser(_testUser);
        when(_timeTrackingRepository.getActiveTimeTrack(_testUser)).thenReturn(actualTimeTrack);

        boolean expected = true;
        boolean result = _validation.validateTimeTrackUpdate(actualTimeTrack);
        Assert.assertEquals(expected, result);
        Mockito.verify(_timeTrackingRepository, times(1)).getActiveTimeTrack(_testUser);
    }

    @Test
    public void validateUpdateTimeTrack_ForNoActiveTimeTrackFoundAndOnlyOverlapTimeTrackHasSameID_ShouldReturnTrue() throws NotFoundException, UserException {
        // init
        TimeTrack anotherTimeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(5), null);
        anotherTimeTrack.setId(1);
        anotherTimeTrack.setUser(_testUser);
        when(_timeTrackingRepository.getActiveTimeTrack(_testUser)).thenThrow(NotFoundException.class);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);

        boolean expected = true;
        boolean result = _validation.validateTimeTrackUpdate(anotherTimeTrack);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void validateUpdateTimeTrack_ForNoActiveTimeTrackAndOneOfOverlapingTimeTracksHasSameId_ShouldReturnFalse() throws UserException, NotFoundException {
        // init
        TimeTrack anotherTimeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(5), null);
        anotherTimeTrack.setId(4);
        anotherTimeTrack.setUser(_testUser);
        _testList.add(anotherTimeTrack);
        when(_timeTrackingRepository.getActiveTimeTrack(_testUser)).thenThrow(NotFoundException.class);
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(_testList);

        boolean expected = false;
        boolean result = _validation.validateTimeTrackUpdate(anotherTimeTrack);
        Assert.assertEquals(expected, result);
    }
}

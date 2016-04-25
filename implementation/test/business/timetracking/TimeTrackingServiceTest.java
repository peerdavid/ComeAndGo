package business.timetracking;

import business.usermanagement.UserException;
import business.notification.NotificationSender;
import business.usermanagement.SecurityRole;
import infrastructure.TimeTrackingRepository;
import infrastructure.InternalUserManagement;
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
 * Unit test example.
 */
public class TimeTrackingServiceTest {

    NotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    InternalUserManagement _userRepository;
    TimeTrackingService _timeTrackService;
    User _testUser;
    Break _testBreak;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, "testBoss");
        _testBreak = new Break(DateTime.now());

        _notificationSenderMock = mock(NotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(InternalUserManagement.class);

        _timeTrackService = new TimeTrackingServiceImpl(_timeTrackingRepository, _notificationSenderMock, _userRepository);
    }


    @Test
    public void come_ForExistingUser_ShouldReturnIdAndCallRepository() throws UserException, TimeTrackException, NotFoundException {
        // Prepare
        when(_timeTrackingRepository.createTimeTrack(any(TimeTrack.class))).thenReturn(7); // If the tested function calls our repository mock, we always return 7
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        int expected = 7;
        int userId = 8;

        // Call
        int actual = _timeTrackService.come(userId);    // Call the function, which should also call the repository

        // Validate
        Mockito.verify(_userRepository, times(2)).readUser(userId); // Check if the function really called our repository
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(any(TimeTrack.class)); // Check if the function really called our repository
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = UserException.class)
    public void come_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws UserException, TimeTrackException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(null);
        int userId = 8;

        // Call
        _timeTrackService.come(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId); // Check if the function really called our repository
    }

    @Test
    public void go_WithComeCalledBefore_ShouldSetToInTimeTrackAndCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // prepare
        TimeTrack timeTrack = new TimeTrack(_testUser);

        timeTrack.setFrom(new DateTime(2016, 4, 7, 8, 0));
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(_testUser);
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertNotEquals(timeTrack.getTo(), null);
    }

    @Test(expected = NotFoundException.class)
    public void go_WithoutComeCalledBefore_ShouldThrowExceptionAndCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(_testUser);
        Mockito.verify(_userRepository, times(1)).readUser(userId);
    }

    @Test(expected = UserException.class)
    public void go_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenThrow(Exception.class);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
    }

    @Test
    public void isActive_ForInactiveUser_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_IfNoTimeTrackIsFound_ShouldCallRepositoryAndReturnFalse() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_IfTimeTrackWasFound_ShouldCallRepositoryAndReturnTrue() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        int userId = 8;
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(1), null);

        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);

        boolean expected = true;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_ForActiveUser_ShouldCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        TimeTrack timeTrack = new TimeTrack(_testUser);
        timeTrack.setFrom(DateTime.now().minusHours(1));
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);

        int userId = 8;
        boolean expected = true;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void isActive_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenThrow(Exception.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void takesBreak_ForUserNotTakingABreak_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void takesBreak_ForUserInBreak_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenReturn(testBreak);

        int userId = 8;
        boolean expected = true;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void takesBreak_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(null);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void creatingBreak_ForUserNotExists_ShouldThrowUserException() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_userRepository.readUser(7)).thenThrow(NotFoundException.class);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_userRepository, times(1)).readUser(userId);
    }


    @Test(expected = UserException.class)
    public void creatingBreak_ForUserNotCameBefore_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_userRepository.readUser(7)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackService, times(1)).isActive(userId);
    }

    @Test(expected = UserException.class)
    public void creatingABreak_WhenUserAlreadyDidBefore_ShouldThrowUserException() throws UserException, NotFoundException, TimeTrackException {
        //prepare
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenReturn(testBreak);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
    }

    @Test(expected = UserException.class)
    public void endBreak_WhenUserDidNotStartBreak_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_userRepository.readUser(7)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(_testUser)).thenThrow(NotFoundException.class);

        int userId = 7;
        _timeTrackService.endBreak(userId);

        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Mockito.verify(_timeTrackService, times(1)).takesBreak(userId);
    }

    @Test(expected = UserException.class)
    public void startBreak_WhenUserDidNotStartWork_ShouldThrowUserExceptionAndCallRepositoryTwice() throws TimeTrackException, NotFoundException, UserException {
        int userId = 7;
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        //Mockito.verify(_timeTrackingRepository, times(2)).readActiveTimeTrack(any(User.class));
        //Mockito.verify(_userRepository, times(1)).readUser(userId);
        _timeTrackService.createBreak(userId);
    }

    @Test(expected = UserException.class)
    public void endBreak_BeforeStartingWork_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        int userId = 7;
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenReturn(testBreak);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        _timeTrackService.endBreak(userId);
    }

    @Test(expected = UserException.class)
    public void endBreak_BeforeStartingABreak_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        int userId = 7;
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);

        _timeTrackService.endBreak(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
    }

    @Test
    public void startBreak_AfterStartOfWork_ShouldSucceedAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        // prepare
        int userId = 8;
        TimeTrack timeTrack = new TimeTrack(_testUser);
        when(_userRepository.readUser(userId)).thenReturn(_testUser);
        // simulate _testUser is not having a break currently
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);
        // simulate _testUser is working
        when(_timeTrackingRepository.readActiveTimeTrack(_testUser)).thenReturn(timeTrack);

        _timeTrackService.createBreak(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).startBreak(any(User.class));
    }

    @Test
    public void endBreak_AfterStartingABreak_ShouldSucceedAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        TimeTrack timeTrack = new TimeTrack(_testUser);
        when(_userRepository.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(_testUser)).thenReturn(timeTrack);
        when(_timeTrackingRepository.readActiveBreak(_testUser)).thenReturn(_testBreak);

        _timeTrackService.endBreak(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).endBreak(_testUser);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(_testUser);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(_testUser);
    }

    @Test(expected = TimeTrackException.class)
    public void getTimeTrackList_ForUserWithNoTimeTracks_ShouldThrowExceptionAndCallRepo() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        when(_userRepository.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class))).thenThrow(TimeTrackException.class);

        _timeTrackService.readTimeTracks(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracks(_testUser);

    }

    @Test(expected = TimeTrackException.class)
    public void getTimeTrackList_WithSpecifiedTimesForUserNotHavingTimeTracks_ShouldThrowExceptionAndCallRepo() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        when(_userRepository.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class), any(DateTime.class), any(DateTime.class)))
            .thenThrow(TimeTrackException.class);

        _timeTrackService.readTimeTracks(userId, DateTime.now().minusDays(1), DateTime.now());
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracks(any(User.class));

    }

    @Test(expected = UserException.class)
    public void addTimeTrack_whichOverlaysToAnother_ShouldThrowUserException() throws UserException {
        // init
        List<TimeTrack> storedList = new ArrayList();
        storedList.add(new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusDays(1), null));
        storedList.add(new TimeTrack(_testUser, DateTime.now().plusDays(3), DateTime.now().plusDays(4), null));
        TimeTrack timeTrackToInsert = new TimeTrack(_testUser, DateTime.now().plusHours(23), DateTime.now().plusHours(50), null);

        // prepare
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(storedList);

        _timeTrackService.createTimeTrack(timeTrackToInsert);
    }

    @Test
    public void addTimeTrack_whichDoesNotOverlayToAnother_ShouldSucceedAndCallRepository() throws UserException {
        // init
        TimeTrack timeTrackToInsert = new TimeTrack(_testUser, DateTime.now().plusHours(6), DateTime.now().plusHours(20), null);

        // prepare
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        _timeTrackService.createTimeTrack(timeTrackToInsert);
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(timeTrackToInsert);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracksOverlay(any(User.class), any(TimeTrack.class));
    }

    @Test
    public void addTimeTrack_withExactly8HoursBetweenFromAndTo_ShouldSucceedAndCallRepository() throws UserException {
        // init
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(8), null);
        when(_timeTrackingRepository.readTimeTracksOverlay(_testUser, timeTrack)).thenReturn(Collections.emptyList());

        _timeTrackingRepository.createTimeTrack(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(timeTrack);
    }

    @Test
    public void editTimeTrack_whichIsTheActiveOneAndOnlyEditsToTime_ShouldSucceedAndCallRepository() throws NotFoundException, UserException {
    }


}

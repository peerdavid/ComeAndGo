package business.timetracking;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import business.notification.InternalNotificationSender;
import business.usermanagement.SecurityRole;
import infrastructure.TimeTrackingRepository;
import javassist.NotFoundException;
import models.Break;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Unit test example.
 */
public class TimeTrackingServiceImplTest {

    InternalNotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    TimeTrackingValidation _validation;
    TimeOffValidation _timeOffValidation;
    InternalUserManagement _internalUserManagement;
    TimeTrackingService _timeTrackService;
    User _testUser;
    User _testAdmin;
    User _testBoss;
    TimeTrack _testTimeTrack;
    Break _testBreak;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 1200);
        _testUser.setId(1);
        _testBreak = new Break(DateTime.now());

        _testTimeTrack = mock(TimeTrack.class);

        _testAdmin = mock(User.class);
        when(_testAdmin.getRole()).thenReturn(SecurityRole.ROLE_ADMIN);
        when(_testAdmin.getId()).thenReturn(10);
        _testBoss = mock(User.class);
        when(_testBoss.getRole()).thenReturn(SecurityRole.ROLE_BOSS);
        when(_testBoss.getId()).thenReturn(20);

        _notificationSenderMock = mock(InternalNotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _internalUserManagement = mock(InternalUserManagement.class);
        _validation = mock(TimeTrackingValidation.class);
        _timeOffValidation = mock(TimeOffValidation.class);

        when(_internalUserManagement.readUser(10)).thenReturn(_testAdmin);
        when(_internalUserManagement.readUser(20)).thenReturn(_testBoss);

        _timeTrackService = new TimeTrackingServiceImpl(_timeTrackingRepository, _validation, _timeOffValidation, _notificationSenderMock, _internalUserManagement);
    }


    @Test
    public void come_ForExistingUser_ShouldReturnIdAndCallRepository() throws UserException, TimeTrackException, NotFoundException {
        // Prepare
        when(_timeTrackingRepository.createTimeTrack(any(TimeTrack.class))).thenReturn(7); // If the tested function calls our repository mock, we always return 7
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        int expected = 7;
        int userId = 8;

        // Call
        int actual = _timeTrackService.come(userId);    // Call the function, which should also call the repository

        // Validate
        Mockito.verify(_internalUserManagement, times(2)).readUser(userId); // Check if the function really called our repository
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(any(TimeTrack.class)); // Check if the function really called our repository
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void come_whileUserHasTimeOff_ShouldFail() throws Exception {
       /** go while user has time of does not need to be tested.
        *    if user can't come, he also can't go
        *    another problem is that if user comes at evening (e.g. one day before timeOff), he would not be
        *    able to leave work at next day.
        */

        // init and test
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        _timeTrackService.come(8);
        Mockito.verify(_timeOffValidation, times(1)).validateComeForDate(any(User.class), any(DateTime.class));
    }

    @Test(expected = UserException.class)
    public void come_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws UserException, TimeTrackException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(null);
        int userId = 8;

        // Call
        _timeTrackService.come(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId); // Check if the function really called our repository
    }

    @Test
    public void go_WithComeCalledBefore_ShouldSetToInTimeTrackAndCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // prepare
        TimeTrack timeTrack = new TimeTrack(_testUser);

        timeTrack.setFrom(new DateTime(2016, 4, 7, 8, 0));
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(2)).readActiveTimeTrack(_testUser);
        Mockito.verify(_internalUserManagement, times(2)).readUser(userId);
        Assert.assertNotEquals(timeTrack.getTo(), null);
    }

    @Test(expected = UserException.class)
    public void go_WithoutComeCalledBefore_ShouldThrowExceptionAndCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(_testUser);
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
    }

    @Test(expected = UserException.class)
    public void go_ForUnregisteredUser_ShouldThrowException() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenThrow(UserException.class);

        int userId = 8;

        // Call
        _timeTrackService.go(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
    }

    @Test
    public void isActive_ForInactiveUser_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_IfNoTimeTrackIsFound_ShouldCallRepositoryAndReturnFalse() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_IfTimeTrackWasFound_ShouldCallRepositoryAndReturnTrue() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        int userId = 8;
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);

        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(1), null);

        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);

        boolean expected = true;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_ForActiveUser_ShouldCallRepository() throws TimeTrackException, NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        TimeTrack timeTrack = new TimeTrack(_testUser);
        timeTrack.setFrom(DateTime.now().minusHours(1));
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenReturn(timeTrack);

        int userId = 8;
        boolean expected = true;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void isActive_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenThrow(UserException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.isActive(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void takesBreak_ForUserNotTakingABreak_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test
    public void takesBreak_ForUserInBreak_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenReturn(testBreak);

        int userId = 8;
        boolean expected = true;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void takesBreak_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenThrow(UserException.class);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _timeTrackService.takesBreak(userId);

        // Validate
        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void creatingBreak_ForUserNotExists_ShouldThrowUserException() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_internalUserManagement.readUser(7)).thenThrow(UserException.class);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
    }


    @Test(expected = UserException.class)
    public void creatingBreak_ForUserNotCameBefore_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_internalUserManagement.readUser(7)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackService, times(1)).isActive(userId);
    }

    @Test(expected = UserException.class)
    public void creatingABreak_WhenUserAlreadyDidBefore_ShouldThrowUserException() throws UserException, NotFoundException, TimeTrackException {
        //prepare
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenReturn(testBreak);

        int userId = 7;
        _timeTrackService.createBreak(userId);

        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveBreak(any(User.class));
    }

    @Test(expected = UserException.class)
    public void endBreak_WhenUserDidNotStartBreak_ShouldThrowUserExceptionAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        //prepare
        when(_internalUserManagement.readUser(7)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveBreak(_testUser)).thenThrow(NotFoundException.class);

        int userId = 7;
        _timeTrackService.endBreak(userId);

        Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
        Mockito.verify(_timeTrackService, times(1)).takesBreak(userId);
    }

    @Test(expected = UserException.class)
    public void startBreak_WhenUserDidNotStartWork_ShouldThrowUserExceptionAndCallRepositoryTwice() throws TimeTrackException, NotFoundException, UserException {
        int userId = 7;
        when(_timeTrackingRepository.readActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);

        //Mockito.verify(_timeTrackingRepository, times(2)).readActiveTimeTrack(any(User.class));
        //Mockito.verify(_internalUserManagement, times(1)).readUser(userId);
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
        when(_internalUserManagement.readUser(userId)).thenReturn(_testUser);
        // simulate _testUser is not having a break currently
        when(_timeTrackingRepository.readActiveBreak(any(User.class))).thenThrow(NotFoundException.class);
        // simulate _testUser is working
        when(_timeTrackingRepository.readActiveTimeTrack(_testUser)).thenReturn(timeTrack);

        _timeTrackService.createBreak(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).updateTimeTrack(any(TimeTrack.class));
    }

    @Test
    public void endBreak_AfterStartingABreak_ShouldSucceedAndCallRepository() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        TimeTrack timeTrack = new TimeTrack(_testUser);
        when(_internalUserManagement.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readActiveTimeTrack(_testUser)).thenReturn(timeTrack);
        when(_timeTrackingRepository.readActiveBreak(_testUser)).thenReturn(_testBreak);

        _timeTrackService.endBreak(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).updateBreak(any(Break.class));
        Mockito.verify(_timeTrackingRepository, times(1)).readActiveTimeTrack(_testUser);
    }

    @Test(expected = TimeTrackException.class)
    public void getTimeTrackList_ForUserWithNoTimeTracks_ShouldThrowExceptionAndCallRepo() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        when(_internalUserManagement.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class))).thenThrow(TimeTrackException.class);

        _timeTrackService.readTimeTracks(userId);
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracks(_testUser);

    }

    @Test(expected = TimeTrackException.class)
    public void getTimeTrackList_WithSpecifiedTimesForUserNotHavingTimeTracks_ShouldThrowExceptionAndCallRepo() throws TimeTrackException, UserException, NotFoundException {
        int userId = 8;
        when(_internalUserManagement.readUser(userId)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class), any(DateTime.class), any(DateTime.class)))
            .thenThrow(TimeTrackException.class);

        _timeTrackService.readTimeTracks(userId, DateTime.now().minusDays(1), DateTime.now());
        Mockito.verify(_timeTrackingRepository, times(1)).readTimeTracks(any(User.class));

    }

    @Test
    public void createTimeTrack_withTimeTrackWeDontCareAbout_ShouldCallValidation() throws Exception {
        // init
        TimeTrack timeTrackToInsert = new TimeTrack(_testUser, DateTime.now().plusHours(23), DateTime.now().plusHours(50), null);

        _timeTrackService.createTimeTrack(timeTrackToInsert, 0, "");
        Mockito.verify(_validation, times(1)).validateTimeTrackInsert(any(TimeTrack.class));
    }

    @Test
    public void createTimeTrack_whichDoesNotOverlayToAnother_ShouldSucceedAndCallRepository() throws Exception {
        // init
        TimeTrack timeTrackToInsert = new TimeTrack(_testUser, DateTime.now().plusHours(6), DateTime.now().plusHours(20), null);

        // prepare
        when(_timeTrackingRepository.readTimeTracksOverlay(any(User.class), any(TimeTrack.class))).thenReturn(Collections.emptyList());
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);

        _timeTrackService.createTimeTrack(timeTrackToInsert, 0, "");
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(timeTrackToInsert);
    }

    @Test
    public void createTimeTrack_withExactly8HoursBetweenFromAndTo_ShouldSucceedAndCallRepository() throws UserException {
        // init
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(8), null);
        when(_timeTrackingRepository.readTimeTracksOverlay(_testUser, timeTrack)).thenReturn(Collections.emptyList());

        _timeTrackingRepository.createTimeTrack(timeTrack);
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(timeTrack);
    }

    @Test
    public void updateTimeTrack_withTimeTrackWeDontCareAbout_ShouldCallValidation() throws Exception {
        TimeTrack timeTrack = new TimeTrack(_testUser, DateTime.now(), DateTime.now().plusHours(8), null);
        timeTrack.setId(1);

        _timeTrackService.updateTimeTrack(timeTrack, 0, "");
        Mockito.verify(_validation, times(1)).validateTimeTrackUpdate(any(TimeTrack.class));
    }

    @Test
    public void deleteTimeTrack_ExecutedByUserWeDoNotCare_ShouldCallUserValidation() throws Exception {
        _timeTrackService.deleteTimeTrack(_testTimeTrack, 0, "");
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManager(0);
    }

    @Test
    public void createTimeTrack_ExecutedByUserWeDoNotCare_ShouldCallUserValidation() throws Exception {
        _timeTrackService.createTimeTrack(_testTimeTrack, 0, "");
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManager(0);
    }

    @Test
    public void creatsdfeTimeTrack_ExecutedByUserWeDoNotCare_ShouldCallUserValidation() throws Exception {
        _timeTrackService.createTimeTrack(_testBoss.getId(), DateTime.now(), DateTime.now().plusHours(10), 0, "");
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManager(0);
    }

    @Test
    public void updateTimeTrack_ExecutedByUserWeDoNotCare_ShouldCallUserValidation() throws Exception {
        _timeTrackService.updateTimeTrack(_testTimeTrack, 0, "");
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManager(0);
    }
}

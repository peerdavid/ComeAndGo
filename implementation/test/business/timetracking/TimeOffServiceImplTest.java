package business.timetracking;

import business.notification.NotificationSender;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.NotAuthorizedException;
import business.usermanagement.UserException;
import infrastructure.TimeOffRepository;
import models.Notification;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by david on 01.05.16.
 */
public class TimeOffServiceImplTest {

    private TimeOffRepository _timeOffRepositoryMock;
    private TimeOffValidation _timeOffValidationMock;
    private NotificationSender _notificationSenderMock;
    private InternalUserManagement _internalUserManagementMock;
    private TimeOffService _testee;
    private User _testEmployeeMock;
    private int _testEmployeeId = 7;
    private User _testBossMock;
    private int _testBossId = 20;
    private TimeOff _timeOffFutureMock;
    private int _timeOffFutureId = 17;
    private TimeOff _timeOffPastMock;
    private int _timeOffPastId = 18;


    @Before
    public void setUp() throws Exception {

        _timeOffValidationMock = mock(TimeOffValidation.class);
        _notificationSenderMock = mock(NotificationSender.class);

        _testBossMock = mock(User.class);
        when(_testBossMock.getId()).thenReturn(_testBossId);

        _testEmployeeMock = mock(User.class);
        when(_testEmployeeMock.getId()).thenReturn(_testEmployeeId);
        when(_testEmployeeMock.get_boss()).thenReturn(_testBossMock);
        _internalUserManagementMock = mock(InternalUserManagement.class);
        when(_internalUserManagementMock.readUser(_testEmployeeId)).thenReturn(_testEmployeeMock);
        when(_internalUserManagementMock.readUser(_testBossId)).thenReturn(_testBossMock);

        _timeOffFutureMock = mock(TimeOff.class);
        when(_timeOffFutureMock.getId()).thenReturn(_timeOffFutureId);
        when(_timeOffFutureMock.getUser()).thenReturn(_testEmployeeMock);
        when(_timeOffFutureMock.getFrom()).thenReturn(DateTime.now().plusDays(1));
        when(_timeOffFutureMock.getTo()).thenReturn(DateTime.now().plusDays(2));

        _timeOffPastMock = mock(TimeOff.class);
        when(_timeOffPastMock.getId()).thenReturn(_timeOffPastId);
        when(_timeOffPastMock.getUser()).thenReturn(_testEmployeeMock);
        when(_timeOffPastMock.getFrom()).thenReturn(DateTime.now().minusDays(2));
        when(_timeOffPastMock.getTo()).thenReturn(DateTime.now().minusDays(1));

        _timeOffRepositoryMock = mock(TimeOffRepository.class);
        when(_timeOffRepositoryMock.readTimeOff(_timeOffFutureId)).thenReturn(_timeOffFutureMock);
        when(_timeOffRepositoryMock.readTimeOff(_timeOffPastId)).thenReturn(_timeOffPastMock);

        _testee = new TimeOffServiceImpl(_timeOffRepositoryMock, _timeOffValidationMock, _notificationSenderMock, _internalUserManagementMock);
    }


    @Test
    public void deleteTimeTrack_FutureTimeTrack_ShouldSucceed() throws Exception{
        _testee.deleteTimeTrack(_testEmployeeId, _timeOffFutureId);
    }


    @Test
    public void deleteTimeTrack_ValidInputData_ShouldSendMessageToBoss() throws Exception{
        _testee.deleteTimeTrack(_testEmployeeId, _timeOffFutureId);

        Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class)); // Check if the function really called our repository
    }


    @Test(expected = NotAuthorizedException.class)
    public void deleteTimeTrack_UserWantsToDeleteTimeOffOfAnotherUser_ShouldFail() throws Exception{
        _testee.deleteTimeTrack(_testEmployeeId + 1, _timeOffFutureId);
    }


    @Test(expected = UserException.class)
    public void deleteTimeTrack_UserWantsToDeleteOldTimeOff_ShouldFail() throws Exception{
        _testee.deleteTimeTrack(_testEmployeeId, _timeOffPastId);
    }


    @Test
    public void rejectHoliday_BossIsBossOfUser_ShouldSucceed() throws Exception{
        _testee.rejectHoliday(_timeOffFutureId, _testBossId);
    }


    @Test
    public void rejectHoliday_BossIsBossOfUser_ShouldSendNotification() throws Exception{
        _testee.rejectHoliday(_timeOffFutureId, _testBossId);
        Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class)); // Check if the function really called our repository
    }


    @Test(expected = NotAuthorizedException.class)
    public void rejectHoliday_BossIsNotBossOfUser_ShouldFail() throws Exception{
        _testee.rejectHoliday(_timeOffFutureId, _testBossId + 1);
    }
}

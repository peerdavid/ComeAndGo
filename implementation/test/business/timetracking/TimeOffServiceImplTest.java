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
    private User _testUserMock;
    private int _testUserId = 7;
    private TimeOff _timeOffFutureMock;
    private int _timeOffFutureId = 17;
    private TimeOff _timeOffPastMock;
    private int _timeOffPastId = 18;


    @Before
    public void setUp() throws Exception {

        _timeOffValidationMock = mock(TimeOffValidation.class);
        _notificationSenderMock = mock(NotificationSender.class);

        _testUserMock = mock(User.class);
        when(_testUserMock.getId()).thenReturn(_testUserId);
        _internalUserManagementMock = mock(InternalUserManagement.class);
        when(_internalUserManagementMock.readUser(_testUserId)).thenReturn(_testUserMock);

        _timeOffFutureMock = mock(TimeOff.class);
        when(_timeOffFutureMock.getId()).thenReturn(_timeOffFutureId);
        when(_timeOffFutureMock.getUser()).thenReturn(_testUserMock);
        when(_timeOffFutureMock.getFrom()).thenReturn(DateTime.now().plusDays(1));
        when(_timeOffFutureMock.getTo()).thenReturn(DateTime.now().plusDays(2));

        _timeOffPastMock = mock(TimeOff.class);
        when(_timeOffPastMock.getId()).thenReturn(_timeOffPastId);
        when(_timeOffPastMock.getUser()).thenReturn(_testUserMock);
        when(_timeOffPastMock.getFrom()).thenReturn(DateTime.now().minusDays(2));
        when(_timeOffPastMock.getTo()).thenReturn(DateTime.now().minusDays(1));

        _timeOffRepositoryMock = mock(TimeOffRepository.class);
        when(_timeOffRepositoryMock.readTimeOff(_timeOffFutureId)).thenReturn(_timeOffFutureMock);
        when(_timeOffRepositoryMock.readTimeOff(_timeOffPastId)).thenReturn(_timeOffPastMock);

        _testee = new TimeOffServiceImpl(_timeOffRepositoryMock, _timeOffValidationMock, _notificationSenderMock, _internalUserManagementMock);
    }


    @Test
    public void deleteTimeTrack_FutureTimeTrack_ShouldSucceed() throws Exception{
        _testee.deleteTimeTrack(_testUserId, _timeOffFutureId);
    }


    @Test
    public void deleteTimeTrack_ValidInputData_ShouldSendMessageToBoss() throws Exception{
        _testee.deleteTimeTrack(_testUserId, _timeOffFutureId);

        Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class)); // Check if the function really called our repository
    }


    @Test(expected = NotAuthorizedException.class)
    public void deleteTimeTrack_UserWantsToDeleteTimeOffOfAnotherUser_ShouldFail() throws Exception{
        _testee.deleteTimeTrack(_testUserId + 1, _timeOffFutureId);
    }


    @Test(expected = UserException.class)
    public void deleteTimeTrack_UserWantsToDeleteOldTimeOff_ShouldFail() throws Exception{
        _testee.deleteTimeTrack(_testUserId, _timeOffPastId);
    }
}

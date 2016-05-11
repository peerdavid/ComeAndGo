package business.reporting;

import business.notification.NotificationSender;
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

    NotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    InternalUserManagement _internalUserManagement;
    User _testUser;
    ReportingService _reporting;
    CollectiveAgreement _collectiveAgreement;
    InternalTimeTracking _internalTimeTrack;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 1200);

        _notificationSenderMock = mock(NotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _internalUserManagement = mock(InternalUserManagement.class);
        _internalTimeTrack = mock(InternalTimeTracking.class);
        _collectiveAgreement = mock(CollectiveAgreement.class);
        _reporting = new ReportingServiceImpl(_internalUserManagement, _collectiveAgreement, _internalTimeTrack);
    }

    @Test
    public void getHoursWorked_WithNoTimeTracks_ShouldSucceedAndCallRepository() throws Exception {
        // Prepare
        when(_internalUserManagement.readUser(8)).thenReturn(_testUser);
        when(_timeTrackingRepository.readTimeTracks(any(User.class), any(DateTime.class), any(DateTime.class))).thenReturn(Collections.emptyList());

        int userId = 8;
        double expected = 0;

        // Call
        double actual = _reporting.readHoursWorked(userId);

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
        double actual = _reporting.readHoursWorked(userId);

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
        double actual = _reporting.readHoursWorked(userId);

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
        _reporting.readHoursWorked(userId);
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
        _reporting.readHoursWorked(userId);
    }

}

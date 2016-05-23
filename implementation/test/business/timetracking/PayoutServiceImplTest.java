package business.timetracking;

import business.notification.InternalNotificationSender;
import business.usermanagement.InternalUserManagement;
import infrastructure.PayoutRepository;
import models.Notification;
import models.Payout;
import models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

/**
 * Created by stefan on 11.05.16.
 */
public class PayoutServiceImplTest {

   private PayoutServiceImpl _testee;

   private PayoutRepository _repository;
   private InternalNotificationSender _notificationSenderMock;
   private InternalUserManagement _internalUserManagementMock;
   private User _testEmployeeMock;
   private int _testEmployeeId = 7;
   private User _testBossMock;
   private int _testBossId = 20;
   private Payout _testPayoutHolidayMock;
   private int _testPayoutHolidayId = 5;
   private Payout _testPayoutOvertimeMock;
   private int _testPayoutOvertimeId = 6;

   @Before
   public void setUp() throws Exception {
      // init notification sender
      _notificationSenderMock = mock(InternalNotificationSender.class);

      // init boss mock
      _testBossMock = mock(User.class);
      when(_testBossMock.getId()).thenReturn(_testBossId);

      // init employee mock
      _testEmployeeMock = mock(User.class);
      when(_testEmployeeMock.getId()).thenReturn(_testEmployeeId);
      when(_testEmployeeMock.getBoss()).thenReturn(_testBossMock);

      // init payout mock for holiday
      _testPayoutHolidayMock = mock(Payout.class);
      when(_testPayoutHolidayMock.getId()).thenReturn(_testPayoutHolidayId);
      when(_testPayoutHolidayMock.getUser()).thenReturn(_testEmployeeMock);
      when(_testPayoutHolidayMock.getType()).thenReturn(PayoutType.HOLIDAY_PAYOUT);

      // init payout mock for overtime
      _testPayoutOvertimeMock = mock(Payout.class);
      when(_testPayoutOvertimeMock.getId()).thenReturn(_testPayoutOvertimeId);
      when(_testPayoutOvertimeMock.getUser()).thenReturn(_testEmployeeMock);
      when(_testPayoutOvertimeMock.getType()).thenReturn(PayoutType.OVERTIME_PAYOUT);

      // init repository
      _repository = mock(PayoutRepository.class);
      when(_repository.createPayout(any(Payout.class))).thenReturn(1);
      when(_repository.readPayout(_testPayoutHolidayId)).thenReturn(_testPayoutHolidayMock);
      when(_repository.readPayout(_testPayoutOvertimeId)).thenReturn(_testPayoutOvertimeMock);
      List<Payout> payoutList = new ArrayList<>();
      payoutList.add(_testPayoutOvertimeMock);
      payoutList.add(_testPayoutHolidayMock);
      when(_repository.readPayouts(_testEmployeeId)).thenReturn(payoutList);
      when(_repository.readPayouts(any(Integer.class))).thenReturn(payoutList);
      when(_repository.readAcceptedPayoutsFromUser(any(Integer.class))).thenReturn(payoutList);

      // init internal user management mock
      _internalUserManagementMock = mock(InternalUserManagement.class);
      when(_internalUserManagementMock.readUser(_testEmployeeId)).thenReturn(_testEmployeeMock);
      when(_internalUserManagementMock.readUser(_testBossId)).thenReturn(_testBossMock);

      // init test case
      _testee = new PayoutServiceImpl(_internalUserManagementMock, _repository, _notificationSenderMock);
   }


   @Test
   public void requestPayoutOvertime_ForUserExists_ShouldCallRepository() throws Exception{
      _testee.requestOvertimePayout(_testEmployeeId, 10, "comment");
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test
   public void requestPayoutHoliday_ForUserExists_ShouldCallRepository() throws Exception {
      _testee.requestHolidayPayout(_testEmployeeId, 10, "comment");
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test(expected = TimeTrackException.class)
   public void acceptHolidayRequest_ForOvertimeRequestId_ShouldFail() throws Exception {
      _testee.acceptHolidayPayout(_testPayoutOvertimeId, _testBossId);
   }

   @Test
   public void acceptHolidayRequest_ForHolidayRequestId_ShouldSucceedAndUpdatePayout() throws Exception {
      _testee.acceptHolidayPayout(_testPayoutHolidayId, _testBossId);
      Mockito.verify(_repository, times(1)).updatePayout(any(Payout.class));
   }

   @Test
   public void acceptHolidayRequest_ForHolidayRequestId_ShouldSucceedAndSendNotification() throws Exception {
      _testee.acceptHolidayPayout(_testPayoutHolidayId, _testBossId);
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test(expected = TimeTrackException.class)
   public void rejectHolidayRequest_ForOvertimeRequestId_ShouldFail() throws Exception {
      _testee.rejectHolidayPayout(_testPayoutOvertimeId, _testBossId);
   }

   @Test
   public void rejectHolidayRequest_ForHolidayRequestId_ShouldSucceedAndUpdatePayout() throws Exception {
      _testee.rejectHolidayPayout(_testPayoutHolidayId, _testBossId);
      Mockito.verify(_repository, times(1)).updatePayout(any(Payout.class));
   }

   @Test
   public void rejectHolidayRequest_ForHolidayRequestId_ShouldSucceedAndSendNotification() throws Exception {
      _testee.rejectHolidayPayout(_testPayoutHolidayId, _testBossId);
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test(expected = TimeTrackException.class)
   public void acceptOvertimeRequest_ForHolidayRequestId_ShouldFail() throws Exception {
      _testee.acceptOvertimePayout(_testPayoutHolidayId, _testBossId);
   }

   @Test
   public void acceptOvertimeRequest_ForOvertimeRequestId_ShouldSucceedAndUpdatePayout() throws Exception {
      _testee.acceptOvertimePayout(_testPayoutOvertimeId, _testBossId);
      Mockito.verify(_repository, times(1)).updatePayout(any(Payout.class));
   }

   @Test
   public void acceptOvertimeRequest_ForOvertimeRequestId_ShouldSucceedAndSendNotification() throws Exception {
      _testee.acceptOvertimePayout(_testPayoutOvertimeId, _testBossId);
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test(expected = TimeTrackException.class)
   public void rejectOvertimeRequest_ForHolidayRequestId_ShouldFail() throws Exception {
      _testee.rejectOvertimePayout(_testPayoutHolidayId, _testBossId);
   }

   @Test
   public void rejectOvertimeRequest_ForOvertimeRequestId_ShouldSucceedAndUpdatePayout() throws Exception {
      _testee.acceptOvertimePayout(_testPayoutOvertimeId, _testBossId);
      Mockito.verify(_repository, times(1)).updatePayout(any(Payout.class));
   }

   @Test
   public void rejectOvertimeRequest_ForOvertimeRequestId_ShouldSucceedAndSendNotification() throws Exception {
      _testee.rejectOvertimePayout(_testPayoutOvertimeId, _testBossId);
      Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any(Notification.class));
   }

   @Test
   public void readPayouts_FromEmployee_ShouldCallRepository() throws Exception {
      int expected = 2;
      int actual = _testee.readPayoutsFromUser(_testEmployeeId).size();

      Mockito.verify(_repository, times(1)).readPayouts(_testEmployeeId);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void readPayout_IdentificatedByPayoutId_ShouldCallRepository() throws Exception {
      Payout expected = _testPayoutHolidayMock;
      Payout actual = _testee.readPayout(_testPayoutHolidayId);

      Mockito.verify(_repository, times(1)).readPayout(_testPayoutHolidayId);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void readPayouts_FromUserWithGivenDates_ShouldCallRepository() throws Exception {
      int expected = 2;
      int actual = _testee.readPayoutsFromUser(_testEmployeeId).size();

      Mockito.verify(_repository, times(1)).readPayouts(any(Integer.class));
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void readAcceptedPayouts_FromUser_ShouldCallRepository() throws Exception {
      int expected = 2;
      int actual = _testee.readAcceptedPayoutsFromUser(_testEmployeeId).size();

      Mockito.verify(_repository, times(1)).readAcceptedPayoutsFromUser(any(Integer.class));
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void updatePayoutRequest_WithUnimportantPayout_ShouldCallRepository() throws Exception {
      _testee.updatePayoutRequest(_testPayoutHolidayMock);
      Mockito.verify(_repository, times(1)).updatePayout(_testPayoutHolidayMock);
   }

   @Test
   public void deletePayoutRequest_WithUnimportantPayout_ShouldCallRepositoryTwice() throws Exception {
      _testee.deletePayout(_testPayoutHolidayId);
      Mockito.verify(_repository, times(1)).readPayout(_testPayoutHolidayId);
      Mockito.verify(_repository, times(1)).deletePayout(_testPayoutHolidayMock);
   }


}

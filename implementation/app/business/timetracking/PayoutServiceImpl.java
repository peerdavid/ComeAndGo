package business.timetracking;

import business.notification.NotificationSender;
import business.notification.NotificationType;
import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import infrastructure.PayoutRepository;
import models.Notification;
import models.Payout;
import models.User;

/**
 * Created by stefan on 07.05.16.
 */
class PayoutServiceImpl implements PayoutService {

   private final InternalUserManagement _userManagement;
   private final InternalTimeTracking _timeTracking;
   private final PayoutRepository _repository;
   private final NotificationSender _notification;

   @Inject
   PayoutServiceImpl(InternalUserManagement userManagement, InternalTimeTracking timeTracking,
                     PayoutRepository repository, NotificationSender notification) {
      _userManagement = userManagement;
      _timeTracking = timeTracking;
      _repository = repository;
      _notification = notification;
   }

   @Override
   public void requestOvertimePayout(int userId, int numberOfHours, String comment) throws Exception {
      // create payout entity
      User employee = _userManagement.readUser(userId);
      Payout payout = new Payout(employee, PayoutType.OVERTIME_PAYOUT, numberOfHours, RequestState.REQUEST_SENT, comment);
      int id = _repository.createPayout(payout);

      // send notification
      User boss = employee.getBoss();
      Notification notification = new Notification(NotificationType.OVERTIME_PAYOUT_REQUEST, employee, boss, id);
      _notification.sendNotification(notification);
   }

   @Override
   public void requestHolidayPayout(int userId, int numberOfDays, String comment) throws Exception {
      // create payout entity
      User employee = _userManagement.readUser(userId);
      Payout payout = new Payout(employee, PayoutType.HOLIDAY_PAYOUT, numberOfDays, RequestState.REQUEST_SENT, comment);
      int id = _repository.createPayout(payout);

      // send notification
      User boss = employee.getBoss();
      Notification notification = new Notification(NotificationType.HOLIDAY_PAYOUT_REQUEST, employee, boss, id);
      _notification.sendNotification(notification);
   }

   @Override
   public void acceptHolidayPayout(int payoutId, int bossId) throws Exception {
      // marks requested holiday payout as accepted
      Payout requestedPayout = _repository.readPayout(payoutId);
      requestedPayout.setState(RequestState.REQUEST_ACCEPTED);
      _repository.updatePayout(requestedPayout);

      // sends notification to employee
      User boss = _userManagement.readUser(bossId);
      Notification notification = new Notification(NotificationType.HOLIDAY_PAYOUT_ACCEPT, boss, requestedPayout.getUser());
      _notification.sendNotification(notification);
   }

   @Override
   public void rejectHolidayPayout(int payoutId, int bossId) throws Exception {
      Payout requestedPayout = _repository.readPayout(payoutId);
      requestedPayout.setState(RequestState.REQUEST_REJECTED);
      _repository.updatePayout(requestedPayout);

      // notify employee about rejected payout
      User boss = _userManagement.readUser(bossId);
      Notification notification = new Notification(NotificationType.HOLIDAY_PAYOUT_REJECT, boss, requestedPayout.getUser());
      _notification.sendNotification(notification);
   }

   @Override
   public void acceptOvertimePayout(int payoutId, int bossId) throws Exception {
      // marks requested holiday payout as accepted
      Payout requestedPayout = _repository.readPayout(payoutId);
      requestedPayout.setState(RequestState.REQUEST_ACCEPTED);
      _repository.updatePayout(requestedPayout);

      // sends notification to employee
      User boss = _userManagement.readUser(bossId);
      Notification notification = new Notification(NotificationType.OVERTIME_PAYOUT_ACCEPT, boss, requestedPayout.getUser());
      _notification.sendNotification(notification);
   }

   @Override
   public void rejectOvertimePayout(int payoutId, int bossId) throws Exception {
      Payout requestedPayout = _repository.readPayout(payoutId);
      requestedPayout.setState(RequestState.REQUEST_REJECTED);
      _repository.updatePayout(requestedPayout);

      // notify employee about rejected payout
      User boss = _userManagement.readUser(bossId);
      Notification notification = new Notification(NotificationType.OVERTIME_PAYOUT_REJECT, boss, requestedPayout.getUser());
      _notification.sendNotification(notification);
   }
}

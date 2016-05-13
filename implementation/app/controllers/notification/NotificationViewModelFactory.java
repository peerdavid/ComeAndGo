package controllers.notification;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.Notification;
import models.TimeOff;
import play.i18n.Messages;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csaq5996 on 4/25/16.
 *
 */
public class NotificationViewModelFactory {

    private TimeTracking _timeTracking;

    @Inject
    public NotificationViewModelFactory(TimeTracking timeTracking) {
        _timeTracking = timeTracking;
    }

    public NotificationViewModel createNotificationViewModel(Notification notification) throws Exception {

        switch (notification.getType()) {

            // Holiday-Cases
            //TODO: fix requests for deleted TimeOffs
            case HOLIDAY_REQUEST: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                String message = notification.getMessage();
                String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                if(message.isEmpty()) {
                    message = Messages.get("notifications.holiday_request") + " " + sender;
                }

                return new HolidayRequestViewModel(
                        notification.getId(),
                        notification.getReferenceId(),
                        message,
                        sender,
                        date,
                        _timeTracking
                );
            }

            case HOLIDAY_ACCEPT: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                return new HolidayAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_accept"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    date,
                    _timeTracking
                );
            }

            case HOLIDAY_REJECT: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                return new HolidayRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_reject"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    date,
                    _timeTracking
                );
            }

            // Holiday-Payout-Cases
            case HOLIDAY_PAYOUT_REQUEST: {

                return new HolidayPayoutRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    Messages.get("notifications.holiday_payout_request"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case HOLIDAY_PAYOUT_ACCEPT: {

                return new HolidayPayoutAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_payout_accept"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case HOLIDAY_PAYOUT_REJECT: {

                return new HolidayPayoutRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_payout_reject"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // Overtime-Payout-Cases
            case OVERTIME_PAYOUT_REQUEST: {

                return new OvertimePayoutRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    Messages.get("notifications.overtime_payout_request"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case OVERTIME_PAYOUT_ACCEPT: {

                return new OvertimePayoutAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.overtime_payout_accept"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case OVERTIME_PAYOUT_REJECT: {

                return new OvertimePayoutRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.overtime_payout_reject"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // Educational-Leave-Cases
            case EDUCATIONAL_LEAVE_REQUEST: {

                String message = notification.getMessage();

                if(notification.getMessage().isEmpty()) {
                    message = Messages.get("notifications.educational_leave_request");
                }

                return new EducationalLeaveRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    message,
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case EDUCATIONAL_LEAVE_ACCEPT: {
                return new EducationalLeaveAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.educational_leave_accept"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case EDUCATIONAL_LEAVE_REJECT: {
                return new EducationalLeaveRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.educational_leave_reject"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // Special-Holiday-Cases
            case SPECIAL_HOLIDAY_REQUEST: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                String message = notification.getMessage();
                String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                if(message.isEmpty()) {
                    message = Messages.get("notifications.special_holiday_request") + " " + sender;
                }

                return new SpecialHolidayRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    message,
                    sender,
                    date,
                    _timeTracking
                );
            }

            case SPECIAL_HOLIDAY_ACCEPT: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                return new SpecialHolidayAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.special_holiday_accept"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    date,
                    _timeTracking
                );
            }

            case SPECIAL_HOLIDAY_REJECT: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                return new SpecialHolidayRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.special_holiday_reject"),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    date,
                    _timeTracking
                );
            }

            // Non-Rejectable Notifications
            case SICK_LEAVE_INFORMATION: {
                TimeOff timeOff = _timeTracking.readTimeOffById(notification.getReferenceId());

                String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                    + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                String message = notification.getMessage();
                String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                if(message.isEmpty()) {
                    message = sender + Messages.get("notifications.sick_leave_information");
                }


                return new SickLeaveViewModel(
                    notification.getId(),
                    message,
                    sender,
                    date,
                    _timeTracking
                );
            }

            case PARENTAL_LEAVE_REQUEST: {
                return new ParentalLeaveViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case BUSINESS_TRIP_INFORMATION: {
                return new BusinessTripInformationViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case FIRE_EMPLOYEE: {
                return new FiredViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case CREATED_TIMETRACK: {
                return new TimeTrackCreatedViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case CHANGED_TIMETRACK: {
                return new TimeTrackChangedViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case DELETED_TIMETRACK: {
                return new TimeTrackDeletedViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // General-Cases
            case INFORMATION: {
                return new InformationViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case ERROR: {
                return new ErrorViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }


            default:
                throw new Exception(Messages.get("exceptions.notificationfactory.notification_type_not_found"));
        }
    }

    public List<NotificationViewModel> createNotificationViewModelList(List<Notification> notificationList) throws Exception {
        List<NotificationViewModel> result = new ArrayList<>();

        for (Notification n:notificationList) {
            NotificationViewModel temp = createNotificationViewModel(n);

            if (temp != null) {
                result.add(temp);
            }
        }

        return result;
    }
}

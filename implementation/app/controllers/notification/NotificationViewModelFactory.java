package controllers.notification;

import business.notification.NotificationType;
import business.timetracking.TimeTrackException;
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
            case HOLIDAY_REQUEST: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    String message = notification.getMessage();
                    String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                    if (message.isEmpty()) {
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
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case HOLIDAY_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new HolidayAcceptViewModel(
                        notification.getId(),
                        Messages.get("notifications.holiday_accept"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case HOLIDAY_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new HolidayRejectViewModel(
                        notification.getId(),
                        Messages.get("notifications.holiday_reject"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            // Holiday-Payout-Cases
            case HOLIDAY_PAYOUT_REQUEST: {

                String message = notification.getMessage();

                return new HolidayPayoutRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    message,
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case HOLIDAY_PAYOUT_ACCEPT: {

                return new HolidayPayoutAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_payout_accept"),
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case HOLIDAY_PAYOUT_REJECT: {

                return new HolidayPayoutRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.holiday_payout_reject"),
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // Overtime-Payout-Cases
            case OVERTIME_PAYOUT_REQUEST: {

                String message = notification.getMessage();

                return new OvertimePayoutRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    message,
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case OVERTIME_PAYOUT_ACCEPT: {

                return new OvertimePayoutAcceptViewModel(
                    notification.getId(),
                    Messages.get("notifications.overtime_payout_accept"),
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            case OVERTIME_PAYOUT_REJECT: {

                return new OvertimePayoutRejectViewModel(
                    notification.getId(),
                    Messages.get("notifications.overtime_payout_reject"),
                    getPayoutAdditionalInfo(notification),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                    _timeTracking
                );
            }

            // Educational-Leave-Cases
            case EDUCATIONAL_LEAVE_REQUEST: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    String message = notification.getMessage();

                    if (notification.getMessage().isEmpty()) {
                        message = Messages.get("notifications.educational_leave_request");
                    }

                    return new EducationalLeaveRequestViewModel(
                        notification.getId(),
                        notification.getReferenceId(),
                        message,
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case EDUCATIONAL_LEAVE_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());
                    return new EducationalLeaveAcceptViewModel(
                        notification.getId(),
                        Messages.get("notifications.educational_leave_accept"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case EDUCATIONAL_LEAVE_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());
                    return new EducationalLeaveRejectViewModel(
                        notification.getId(),
                        Messages.get("notifications.educational_leave_reject"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            // Special-Holiday-Cases
            case SPECIAL_HOLIDAY_REQUEST: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    String message = notification.getMessage();
                    String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                    if (message.isEmpty()) {
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
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case SPECIAL_HOLIDAY_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new SpecialHolidayAcceptViewModel(
                        notification.getId(),
                        Messages.get("notifications.special_holiday_accept"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case SPECIAL_HOLIDAY_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new SpecialHolidayRejectViewModel(
                        notification.getId(),
                        Messages.get("notifications.special_holiday_reject"),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            // Non-Rejectable Notifications
            case SICK_LEAVE_INFORMATION: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    String message = notification.getMessage();
                    String sender = notification.getSender().getFirstName() + " " + notification.getSender().getLastName();

                    if (message.isEmpty()) {
                        message = sender + Messages.get("notifications.sick_leave_information");
                    }


                    return new SickLeaveViewModel(
                        notification.getId(),
                        message,
                        sender,
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case PARENTAL_LEAVE_REQUEST: {

                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {

                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);
                    String message = notification.getMessage();

                    if(message.isEmpty()){
                        message = Messages.get("notifications.parental_leave_request");
                    }

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new ParentalLeaveViewModel(
                        notification.getId(),
                        message,
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
            }

            case BUSINESS_TRIP_INFORMATION: {
                int timeOffId = notification.getReferenceId();

                if(isTimeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);
                    String message = notification.getMessage();

                    if(message.isEmpty()){
                        message = Messages.get("notifications.business_trip_information");
                    }

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new BusinessTripInformationViewModel(
                        notification.getId(),
                        message,
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return timeOffInvalidInformation(notification);
                }
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

    private boolean isTimeOffValid(int timeOffId) throws Exception {
        boolean result = true;

        try {
            _timeTracking.readTimeOffById(timeOffId);
        } catch (TimeTrackException t) {
            result = false;
        }

        return result;
    }

    private InformationViewModel timeOffInvalidInformation(Notification notification){
        return new InformationViewModel(
            notification.getId(),
            Messages.get("notifications.information_timeoffinvalid"),
            notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
            _timeTracking
        );
    }

    private String getPayoutAdditionalInfo(Notification notification) throws Exception {

        String additionalInfo = null;

        switch (notification.getType()){
            case HOLIDAY_PAYOUT_REQUEST:
            case HOLIDAY_PAYOUT_ACCEPT:
            case HOLIDAY_PAYOUT_REJECT:

                try {
                    int amount = _timeTracking.readPayout(notification.getReferenceId()).getAmount();
                    additionalInfo = Messages.get(
                        "notifications.holiday_payout_request",
                        notification.getSender().getFirstName(),
                        amount
                    );
                } catch (TimeTrackException e){
                    additionalInfo = Messages.get("notifications.payout_entry_not_found");
                }
                break;

            case OVERTIME_PAYOUT_REQUEST:
            case OVERTIME_PAYOUT_ACCEPT:
            case OVERTIME_PAYOUT_REJECT:

                try {
                    int amount = _timeTracking.readPayout(notification.getReferenceId()).getAmount();
                    additionalInfo = Messages.get(
                        "notifications.overtime_payout_request",
                        notification.getSender().getFirstName(),
                        amount
                    );
                } catch (TimeTrackException e){
                    additionalInfo = Messages.get("notifications.payout_entry_not_found");
                }
                break;
        }

        return additionalInfo;
    }
}

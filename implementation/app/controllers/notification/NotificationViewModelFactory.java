package controllers.notification;

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

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case HOLIDAY_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case HOLIDAY_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
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
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case EDUCATIONAL_LEAVE_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case EDUCATIONAL_LEAVE_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            // Special-Holiday-Cases
            case SPECIAL_HOLIDAY_REQUEST: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case SPECIAL_HOLIDAY_ACCEPT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case SPECIAL_HOLIDAY_REJECT: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            // Non-Rejectable Notifications
            case SICK_LEAVE_INFORMATION: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
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
                    return null;
                }
            }

            case PARENTAL_LEAVE_REQUEST: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new ParentalLeaveViewModel(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return null;
                }
            }

            case BUSINESS_TRIP_INFORMATION: {
                int timeOffId = notification.getReferenceId();

                if(timeOffValid(timeOffId)) {
                    TimeOff timeOff = _timeTracking.readTimeOffById(timeOffId);

                    String date = DateTimeUtils.dateTimeToDateString(timeOff.getFrom())
                        + " - " + DateTimeUtils.dateTimeToDateString(timeOff.getTo());

                    return new BusinessTripInformationViewModel(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        date,
                        _timeTracking
                    );
                } else {
                    return null;
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

    private boolean timeOffValid(int timeOffId) throws Exception {
        boolean result = true;

        try {
            _timeTracking.readTimeOffById(timeOffId);
        } catch (TimeTrackException t) {
            result = false;
        }

        return result;
    }
}

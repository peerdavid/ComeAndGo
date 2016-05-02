package controllers.notification;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.Notification;

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

            case HOLIDAY_REQUEST:
                return new HolidayRequestViewModel(
                        notification.getId(),
                        notification.getReferenceId(),
                        notification.getMessage(),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case HOLIDAY_ACCEPT:
                return new HolidayAcceptViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case HOLIDAY_REJECT:
                return new HolidayRejectViewModel(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );


            case SICK_LEAVE_INFORMATION:
                return new SickLeaveViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case SPECIAL_HOLIDAY_REQUEST:
                return new SpecialHolidayRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case SPECIAL_HOLIDAY_ACCEPT:
                return new SpecialHolidayAcceptViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case SPECIAL_HOLIDAY_REJECT:
                return new SpecialHolidayRejectViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case INFORMATION:
                return new InformationViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );

            case ERROR:
                return new ErrorViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName(),
                        _timeTracking
                );



            default:
                return null;
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

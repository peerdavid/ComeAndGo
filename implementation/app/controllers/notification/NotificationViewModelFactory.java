package controllers.notification;

import models.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csaq5996 on 4/25/16.
 *
 */
public class NotificationViewModelFactory {

    public static NotificationViewModel createNotificationViewModel(Notification notification) throws Exception {

        switch (notification.getType()) {

            case HOLIDAY_REQUEST:
                return new HolidayRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            case HOLIDAY_ACCEPT:
                return new HolidayAcceptViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            case HOLIDAY_REJECT:
                return new HolidayRejectViewModel(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );


            case SICK_LEAVE_INFORMATION:
                return new SickLeaveViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            case SPECIAL_HOLIDAY_REQUEST:
                return new SpecialHolidayRequestViewModel(
                    notification.getId(),
                    notification.getReferenceId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            case SPECIAL_HOLIDAY_ACCEPT:
                return new SpecialHolidayAcceptViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            case SPECIAL_HOLIDAY_REJECT:
                return new SpecialHolidayRejectViewModel(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getSender().getFirstName() + " " + notification.getSender().getLastName()
                );

            default:
                return null;
                //break;
                //throw new Exception("Unknown notification type");
        }
    }

    public static List<NotificationViewModel> createNotificationViewModelList(List<Notification> notificationList) throws Exception {
        List<NotificationViewModel> result = new ArrayList<>();

        /*if (notificationList.isEmpty()) {
            throw new Exception("NotificationList empty");
        }*/

        for (Notification n:notificationList) {
            result.add(createNotificationViewModel(n));
        }

        return result;
    }
}

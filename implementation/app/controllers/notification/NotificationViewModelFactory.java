package controllers.notification;

import business.timetracking.TimeTracking;
import models.Notification;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class NotificationViewModelFactory {

    public static NotificationViewModel createNotificationViewModel(Notification notification, TimeTracking timeTracking) throws Exception {

        switch (notification.getType()){

            case HOLIDAY_REQUEST:
                //timeTracking.readHolidayRequest(notification.referenceId);
                return new HolidayRequestViewModel(timeTracking);

            case SICK_LEAVE_INFORMATION:
                return new SickLeaveViewModel();

            default:
                throw new Exception("Unknown notification type");
        }
    }
}

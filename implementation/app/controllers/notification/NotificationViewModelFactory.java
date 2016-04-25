package controllers.notification;

import business.timetracking.TimeTracking;
import models.Notification;
import net.sf.ehcache.search.expression.Not;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class NotificationViewModelFactory {

    public static NotificationViewModel createNotificationViewModel(Notification notification, TimeTracking timeTracking) throws Exception {

        switch (notification.getType()){

            case HOLIDAY_REQUEST:
                //timeTracking.readHolidayRequest(notification.referenceId);
                return new HolidayRequestViewModel(timeTracking, notification.getId(), 0, notification.getMessage(),notification.isRead());

            case SICK_LEAVE_INFORMATION:
                return new SickLeaveViewModel(notification.getId(),0,notification.getMessage(),notification.isRead());

            default:
                throw new Exception("Unknown notification type");
        }
    }

    public static List<NotificationViewModel> createNotificationViewModelList(List<Notification> notificationList, TimeTracking timeTracking) throws Exception {
        List<NotificationViewModel> result = new ArrayList<NotificationViewModel>();

        if(notificationList.isEmpty()){
            throw new Exception("hallo");
        }

        for(Notification n:notificationList){
            result.add(createNotificationViewModel(n,timeTracking));
        }

        return result;
    }
}

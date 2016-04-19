package business.notification;

import business.UseCases;
import models.Notification;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationReader extends UseCases {
    List<Notification> getNotifictionsForUser(String userName);

    void setNotificationAsRead(int NotificationId);
}

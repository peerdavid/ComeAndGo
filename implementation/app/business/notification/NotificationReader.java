package business.notification;

import business.NotificationException;
import business.UseCases;
import models.Notification;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationReader extends UseCases {
    List<Notification> getUnreadNotifictionsForUser(String userName) throws NotificationException;

    List<Notification> getReadNotifictionsForUser(String userName) throws NotificationException;

    void setNotificationAsRead(int NotificationId) throws NotificationException;
}

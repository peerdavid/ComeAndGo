package business.notification;

import business.NotificationException;
import business.UseCases;
import models.Notification;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationReader extends UseCases {
    List<Notification> getUnreadNotificationsForUser(int userId) throws NotificationException;

    List<Notification> getReadNotificationsForUser(int userId) throws NotificationException;

    void setNotificationAsRead(int NotificationId) throws NotificationException;

    int getNumberOfUnreadNotifications(int userId) throws NotificationException;
}

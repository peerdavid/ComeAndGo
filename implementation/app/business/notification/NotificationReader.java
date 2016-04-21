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

   /**
    *
    * @param userId
    * @param amount of expected list elements
    * @return list of notifications sent to user, which are specified by userId
    * @throws NotificationException
    */
    List<Notification> getReadNotificationsForUser(int userId, int amount) throws NotificationException;

   /**
    * @param userId
    * @param amount of expected list elements
    * @return outgoing notifications which are sent from user specified by userId
    * @throws NotificationException
    */
    List<Notification> getSentNotifications(int userId, int amount) throws NotificationException;

    void accept(int notificationId) throws NotificationException;

    void reject(int notificationId) throws NotificationException;

    void setNotificationAsRead(int NotificationId) throws NotificationException;

    int getNumberOfUnreadNotifications(int userId) throws NotificationException;
}

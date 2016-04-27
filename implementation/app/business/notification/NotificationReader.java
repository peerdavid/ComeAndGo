package business.notification;

import business.UseCases;
import models.Notification;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationReader extends UseCases {
    List<Notification> readUnseenNotifications(int userId) throws NotificationException;

   /**
    *
    * @param userId
    * @param amount of expected list elements
    * @return list of notifications sent to user, which are specified by userId
    * @throws NotificationException
    */
    List<Notification> readSeenNotifications(int userId, int amount) throws NotificationException;

   /**
    * @param userId
    * @param amount of expected list elements
    * @return outgoing notifications which are sent from user specified by userId
    * @throws NotificationException
    */
    List<Notification> readSentNotifications(int userId, int amount) throws NotificationException;


    void updateNotificationAsRead(int notificationId) throws NotificationException;

    int readNumberOfUnseenNotifications(int userId) throws NotificationException;
}

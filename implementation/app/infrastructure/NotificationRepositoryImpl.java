package infrastructure;

import business.NotificationException;
import business.notification.NotificationType;
import com.avaje.ebean.Ebean;
import models.Notification;
import models.User;

import java.util.Collections;
import java.util.List;

/**
 * Created by paz on 19.04.16.
 */
public class NotificationRepositoryImpl implements NotificationRepository {
    @Override
    public void createNotification(Notification notification) {
        Ebean.save(notification);
    }

    @Override
    public Notification readNotificationById(int notificationId) throws NotificationException {
        Notification notification =
                Ebean.find(Notification.class)
                .where().eq("id", notificationId)
                .findUnique();

        if(notification != null) {
            return notification;
        }
        throw new NotificationException("no notification with that id found");
    }

    @Override
    public void deleteNotification(Notification toDelete) {
        toDelete.makeInvisible();   // sets flag in Notification object to make this notification invisible
        Ebean.update(toDelete);
    }

    @Override
    public void updateNotification(Notification toUpdate) {
        Ebean.update(toUpdate);
    }

    @Override
    public List<Notification> getUnreadNotificationsForUser(User user) throws NotificationException {
        List<Notification> result =
                Ebean.find(Notification.class)
                .where().eq("_receiver_id", user.getId())   // filter notifications for user
                .where().eq("haveseen", false)              // filter for only unread notes
                .orderBy("created desc")                    // the newer the higher in the list
                .findList();
        if(result != null) {
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Notification> getReadNotificationsForUser(User user, int amount) throws NotificationException {
        if(amount <= 0) {
            amount = 100;
        }

        List<Notification> result =
                Ebean.find(Notification.class)
                        .where().eq("_receiver_id", user.getId())
                        .where().eq("haveseen", true)
                        .orderBy("created desc")
                        .setMaxRows(amount)
                        .findList();
        if(result != null) {
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Notification> getSentNotifications(User user, int amount) throws NotificationException {
        if(amount <= 0) {
            amount = 100;
        }

        List<Notification> result =
            Ebean.find(Notification.class)
            .where().eq("_sender_id", user.getId())
            .orderBy("created desc")
            .setMaxRows(amount)
            .findList();

        if(result != null) {
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public void markAsAccepted(Notification accept) throws NotificationException {
        accept.setAccepted(true);
        updateNotification(accept);
    }

    @Override
    public void markAsRejected(Notification reject) throws NotificationException {
        reject.setAccepted(false);
        markAsRead(reject);
    }

    @Override
    public void markAsRead(Notification read) throws NotificationException {
        read.setRead(true);
        updateNotification(read);
    }

    @Override
    public int getNumberOfUnreadNotifications(User user) throws NotificationException {
        return Ebean.find(Notification.class)
            .where().eq("_receiver_id", user.getId())
            .where().eq("haveseen", false)
            .findRowCount();
    }
}

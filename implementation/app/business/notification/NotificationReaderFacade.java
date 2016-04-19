package business.notification;

import models.Notification;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public class NotificationReaderFacade implements NotificationReader {

    public List<Notification> getNotifictionsForUser(String userName) {
        return null;
    }

    @Override
    public void setNotificationAsRead(int NotificationId) {

    }
}

package business.notification;

import models.Notification;

/**
 * Created by david on 22.03.16.
 */
public class NotificationReaderFacade implements NotificationReader {
    @Override
    public Notification getNotifictionForUser(int userId) {
        return new Notification("Hallo", null, null, false);
    }
}

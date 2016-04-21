package models;

import business.NotificationException;
import business.UserException;
import business.notification.NotificationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.i18n.Messages;

/**
 * Created by paz on 21.04.16.
 */
public class NotificationTest {

    Notification testNotification;

    @Before
    public void Setup() throws NotificationException {
        testNotification = new Notification(NotificationType.INFORMATION, "text", null, null, null, null);
    }

    @Test(expected = NotificationException.class)
    public void createNotification_WithTooLongMessage_ShouldFail() throws NotificationException {

        testNotification.setMessage("mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$");
    }

}
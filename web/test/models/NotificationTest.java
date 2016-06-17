package models;

import business.notification.NotificationException;
import business.notification.NotificationType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by paz on 21.04.16.
 */
public class NotificationTest {

    Notification testNotification;

    @Before
    public void Setup() throws NotificationException {
        testNotification = new Notification(NotificationType.INFORMATION, "text", null, null);
    }

    @Test(expected = NotificationException.class)
    public void createNotification_WithTooLongMessage_ShouldFail() throws NotificationException {

        testNotification.setMessage("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
            + "Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis "
            + "dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque "
            + "eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, "
            + "aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, "
            + "justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt.");
    }

    @Test
    public void createNotification_WithValidMessageLength_ShouldSucceed() throws NotificationException {
        testNotification.setMessage("hello world");
    }

}

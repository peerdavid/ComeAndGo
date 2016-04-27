package controllers.notification;

/**
 * Created by csaq5996 on 4/25/16.
 */
public interface NotificationViewModel {

    int getTimeOffId();

    int getNotificationId();

    boolean hasRead();

    String getIcon();

    String getHeader();

    String getMessage();

    String getSender();

    boolean isRejectable();

    void accept();
}

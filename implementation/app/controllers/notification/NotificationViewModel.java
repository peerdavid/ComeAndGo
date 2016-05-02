package controllers.notification;

/**
 * Created by csaq5996 on 4/25/16.
 */
public interface NotificationViewModel {

    int getNotificationId();

    String getIcon();

    String getHeader();

    String getMessage();

    String getSender();

    String getDate();

    boolean isRejectable();

    void accept(int userId) throws Exception;

    void reject(int userId) throws Exception;
}

package controllers.notification;

import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class SickLeaveViewModel implements NotificationViewModel{

    private int notificationId;
    private int timeOffId;

    private String message;

    private boolean read;


    public SickLeaveViewModel(int notificationId, int timeOffId, String message, boolean read){

        this.notificationId = notificationId;
        this.timeOffId = timeOffId;
        this.message = message;
        this.read = read;
    }

    @Override
    public int getTimeOffId() {
        return timeOffId;
    }

    @Override
    public int getNotificationId() {
        return notificationId;
    }

    @Override
    public boolean hasRead() {
        return read;
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.sickleave");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.sickleave");
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept() {
        this.read = true;
    }
}

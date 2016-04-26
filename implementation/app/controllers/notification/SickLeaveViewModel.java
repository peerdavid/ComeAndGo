package controllers.notification;

import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class SickLeaveViewModel extends BasicViewModel implements NotificationViewModel{

    private int timeOffId;

    public SickLeaveViewModel(int notificationId, int timeOffId, String message, String sender, boolean read){

        super(notificationId, message,sender,read);

        this.timeOffId = timeOffId;

    }

    @Override
    public int getTimeOffId() {
        return timeOffId;
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
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept() {
        this.read = true;
    }
}

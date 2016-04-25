package controllers.notification;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class SickLeaveViewModel implements NotificationViewModel{
    @Override
    public int getTimeOffId() {
        return 0;
    }

    @Override
    public int getNotificationId() {
        return 0;
    }

    @Override
    public boolean hasRead() {
        return false;
    }

    @Override
    public String getIcon() {
        return "hotel";
    }

    @Override
    public String getHeader() {
        return "SICK LEAVE";
    }

    @Override
    public String getMessage() {
        return "Bin leima am schpeibn";
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept() {

    }
}

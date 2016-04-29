package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/29/16.
 */
public class HolidayAcceptViewModel extends BasicViewModel {

    private int _timeOffId;

    public HolidayAcceptViewModel(TimeTracking timeTracking, int id, int referenceId, String message, String sender, boolean read) {
        super(id,message,sender,read);

        _timeOffId = referenceId;
    }

    @Override
    public int getTimeOffId() {
        return _timeOffId;
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holiday.accept");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holiday");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept() {
        getNotificationId();
    }
}

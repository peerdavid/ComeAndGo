package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class HolidayRequestViewModel extends BasicViewModel {

    private TimeTracking _timeTracking;

    private int _timeOffId;


    public HolidayRequestViewModel(TimeTracking timeTracking, int _notificationId, int _timeOffId, String message, String sender, boolean read) {

        super(_notificationId,message,sender,read);

        this._timeOffId = _timeOffId;

        _timeTracking = timeTracking;

    }

    @Override
    public int getTimeOffId() {
        return _timeOffId;
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holidayrequest");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holiday");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept() {
        super.setRead(true);
        //_timeTracking.acceptHolidayRequest();
    }

    public void reject() {
        super.setRead(false);
        //_timeTracking.rejectHolidayRequest();
    }
}

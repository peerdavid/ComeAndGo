package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class HolidayRequestViewModel extends BasicViewModel {

    private int _timeOffId;

    public HolidayRequestViewModel(int _notificationId, int _timeOffId, String message, String sender, TimeTracking timeTracking) {

        super(_notificationId, message, sender, timeTracking);

        this._timeOffId = _timeOffId;

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
    public void accept(int userId) throws Exception {
        _timeTracking.acceptHoliday(_timeOffId, userId);
    }

    public void reject(int userId) throws Exception {
        _timeTracking.rejectHoliday(_timeOffId, userId);
    }
}

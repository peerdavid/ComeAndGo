package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class SpecialHolidayRequestViewModel extends BasicViewModel {

    private int _referenceId;

    public SpecialHolidayRequestViewModel(int id, int referenceId, String message, String sender, TimeTracking timeTracking) {
        super(id, message, sender, timeTracking);
        this._referenceId = referenceId;

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.specialholidayrequest");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.specialholiday");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept(int userId) throws Exception {
        _timeTracking.acceptSpecialHoliday(_referenceId, userId);
    }

    public void reject(int userId) throws Exception {
        _timeTracking.rejectSpecialHoliday(_referenceId, userId);
    }
}

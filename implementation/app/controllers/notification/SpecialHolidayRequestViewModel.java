package controllers.notification;

import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class SpecialHolidayRequestViewModel extends BasicViewModel {

    private int _referenceId;

    public SpecialHolidayRequestViewModel(int id, int referenceId, String message, String sender) {
        super(id,message,sender);
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
        timeTracking.acceptSpecialHoliday(_referenceId,userId);
    }

    public void reject(int userId) throws Exception {
        timeTracking.rejectSpecialHoliday(_referenceId,userId);
    }
}

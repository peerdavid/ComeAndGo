package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class SpecialHolidayRejectViewModel extends BasicViewModel {

    public SpecialHolidayRejectViewModel(int id, String message, String sender, TimeTracking timeTracking) {
        super(id, message, sender, timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holiday.reject");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.specialholiday");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept(int userId) {

    }
}
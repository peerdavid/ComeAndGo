package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/29/16.
 */
public class HolidayAcceptViewModel extends BasicViewModel {

    public HolidayAcceptViewModel(int id, String message, String sender, String date, TimeTracking timeTracking) {

        super(id, message, sender, date, timeTracking);

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

}

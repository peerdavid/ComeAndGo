package controllers.notification;

import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/29/16.
 */
public class HolidayRejectViewModel extends BasicViewModel {

    public HolidayRejectViewModel(int id, String message, String sender) {
        super(id,message,sender);
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holiday.reject");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holiday");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    public void accept(int userId) {

    }
}

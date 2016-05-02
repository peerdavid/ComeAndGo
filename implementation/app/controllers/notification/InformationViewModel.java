package controllers.notification;

import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class InformationViewModel extends BasicViewModel {

    public InformationViewModel(int notificationId, String message, String sender) {
        super(notificationId, message, sender);
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.information");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.information");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept(int userId) throws Exception {

    }
}

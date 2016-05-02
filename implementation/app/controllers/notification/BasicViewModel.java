package controllers.notification;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import play.i18n.Messages;

/**
 * Created by Leonhard on 26.04.2016.
 */
public abstract class BasicViewModel implements NotificationViewModel {

    private int _notificationId;

    private String _message;
    private String _sender;

    protected TimeTracking timeTracking;

    @Inject
    public BasicViewModel(int notificationId, String message, String sender) {
        _notificationId = notificationId;

        _message = message;
        _sender = sender;

        this.timeTracking = timeTracking;
    }

    public int getNotificationId(){
        return _notificationId;
    }

    public String getMessage(){
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getSender() {
        return _sender;
    }

    public void reject(int userId) throws Exception {
        throw new Exception(Messages.get("exceptions.notifications.invalid_action"));
    }

}

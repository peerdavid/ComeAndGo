package controllers.notification;

/**
 * Created by Leonhard on 26.04.2016.
 */
public abstract class BasicViewModel implements NotificationViewModel {

    private int _notificationId;

    private String _message;
    private String _sender;

    private boolean _read;

    public BasicViewModel(int notificationId, String message, String sender, boolean read) {
        _notificationId = notificationId;

        _message = message;
        _sender = sender;

        _read = read;
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

    public String getSender(){
        return _sender;
    }

    public boolean hasRead(){
        return _read;
    }

    public void setRead(boolean read) {
        _read = read;
    }
}

package controllers.notification;

/**
 * Created by Leonhard on 26.04.2016.
 */
public class BasicViewModel {

    protected int notificationId;

    protected String message;
    protected String sender;

    protected boolean read;

    public BasicViewModel(int notificationId, String message, String sender, boolean read){
        this.notificationId = notificationId;

        this.message = message;
        this.sender = sender;

        this.read = read;
    }

    public int getNotificationId(){
        return notificationId;
    }

    public String getMessage(){
        return message;
    }

    public String getSender(){
        return sender;
    }

    public boolean hasRead(){
        return read;
    }
}

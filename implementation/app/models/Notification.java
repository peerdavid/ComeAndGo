package models;

import business.UserException;
import business.notification.NotificationType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by david on 22.03.16.
 */
public class Notification {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    private NotificationType _type;

    @Column(name = "message")
    private String _message;

    @Column(name = "from_user")
    private String _fromUser;

    @Column(name = "to_user")
    private String _toUser;

    //private String link; // Check if this is a good idea -> better use a enum with a type
    @Column(name = "read")
    private boolean _read;

    public Notification(NotificationType type, String message, String fromUser, String toUser) throws UserException {

        this.setType(type);
        this.setMessage(message);
        this.setFromUser(fromUser);
        this.setToUser(toUser);
        _read = false;
    }

    public int getId() {
        return id;
    }

    public NotificationType get_type() {
        return _type;
    }

    public void setType(NotificationType type) {
        this._type = type;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) throws UserException {
        if (message.length() > 150) {
            throw new UserException("exceptions.notification.message_length");
        }
        this._message = message;
    }

    public String getFromUser() {
        return _fromUser;
    }

    public void setFromUser(String username) throws UserException {
        if (username.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.username_format");
        }
        this._fromUser = username;
    }

    public String getToUser() {
        return _toUser;
    }

    public void setToUser(String username) throws UserException {
        if (username.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.username_format");
        }
        this._toUser = username;
    }

    public boolean isRead() {
        return _read;
    }

    public void setRead(boolean read) {
        this._read = read;
    }
}

package models;

import business.NotificationException;
import business.UserException;
import business.notification.NotificationType;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.i18n.Messages;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;

/**
 * Created by david on 22.03.16.
 */
@Entity
public class Notification extends Model {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private NotificationType _type;

    @Column(name = "message")
    @Constraints.MaxLength(150)
    private String _message;

    @Column(name = "from_user")
    @ManyToOne()
    @NotNull
    private User _fromUser;

    @Column(name = "to_user")
    @ManyToOne
    @NotNull
    private User _toUser;

    @Column(name = "read")
    private boolean _read;

    @Column(name = "accepted")
    private boolean _accepted;

    @Column(name = "vis_from_user")
    private boolean _visibleForFromUser = false;

    @Column(name = "vis_to_user")
    private boolean _visibleForToUser = true;

    // Use standard messages for each Notification Type
    public Notification(NotificationType type, User fromUser, User toUser) throws NotificationException {

        this.setType(type);
        this.setMessage(getStandardMessage(this));
        this.setFromUser(fromUser);
        this.setToUser(toUser);
        _read = false;
        _accepted = false;
    }

    public Notification(NotificationType type, String message, User fromUser, User toUser) throws NotificationException {

        this.setType(type);
        this.setMessage(message);
        this.setFromUser(fromUser);
        this.setToUser(toUser);
        _read = false;
        _accepted = false;
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

    public void setMessage(String message) throws NotificationException {
        if (message.length() > 150) {
            throw new NotificationException("exceptions.notification.message_length");
        }
        this._message = message;
    }

    public User getFromUser() {
        return _fromUser;
    }

    public void setFromUser(User user) throws NotificationException {
        this._fromUser = user;
    }

    public User getToUser() {
        return _toUser;
    }

    public void setToUser(User user) throws NotificationException {
        this._toUser = user;
    }

    public boolean isRead() {
        return _read;
    }

    public void setRead(boolean read) {
        this._read = read;
    }

    public boolean isAccepted() {
        return _accepted;
    }

    public void setAccepted(boolean accepted) {
        this._accepted = accepted;
    }

    public void setVisibleToUser(boolean visible) {
        _visibleForToUser = visible;
    }

    public boolean getVisibleForToUser() {
        return _visibleForToUser;
    }

    public static String getStandardMessage(Notification notification) {
        return Messages.get("notifications." + notification.get_type().name().toLowerCase(), notification.getFromUser());
    }
}

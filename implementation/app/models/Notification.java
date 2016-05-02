package models;

import business.notification.NotificationException;
import business.notification.NotificationType;
import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import play.data.validation.Constraints;
import play.i18n.Messages;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by david on 22.03.16.
 */
@Entity
public class Notification extends Model {

    // DON'T rename column name _id, otherwise limit functions won't work anymore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _id;

    @Column(name = "type")
    private NotificationType _type;

    @Column(name = "message", columnDefinition = "varchar(150)")
    @Constraints.MaxLength(150)
    private String _message;

    @Column(name = "_sender_id")
    @ManyToOne()
    @NotNull
    private User _sender;

    @Column(name = "_receiver_id")
    @ManyToOne()
    @NotNull
    private User _receiver;

    @Column(name = "seen")
    private Boolean _read;

    @Column(name = "created", columnDefinition = "datetime")
    private DateTime _createdOn;

    @Column(name = "reference_id")
    private int _referenceId;

    // Use standard messages for each Notification Type
    public Notification(NotificationType type, User sender, User receiver) throws NotificationException {
        this(type, "", sender, receiver);
    }

    public Notification(NotificationType type, String message, User sender, User receiver) throws NotificationException {
        setType(type);
        setMessage(message);
        setSender(sender);
        setReceiver(receiver);
        setRead(false);
        _createdOn = DateTime.now();
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public NotificationType getType() {
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

    public User getSender() {
        return _sender;
    }

    public void setSender(User user) throws NotificationException {
        this._sender = user;
    }

    public User getReceiver() {
        return _receiver;
    }

    public void setReceiver(User user) throws NotificationException {
        this._receiver = user;
    }

    public boolean isRead() {
        return _read;
    }

    public void setRead(boolean read) {
        this._read = read;
    }

    public int getReferenceId() {
        return _referenceId;
    }

    public void setReferenceId(int referenceId) {
        this._referenceId = referenceId;
    }
}

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
    @Column(name = "_id")
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

    @Column(name = "haveseen")
    private Boolean _read;

    @Column(name = "accepted")
    private Boolean _accepted;


    @Column(name = "isvisible")
    private Boolean _isVisible;

    @Column(name = "requeststart", columnDefinition = "date")
    private LocalDate _requestedStartDate = null;

    @Column(name = "requestend", columnDefinition = "date")
    private LocalDate _requestedEndDate = null;

    @Column(name = "created", columnDefinition = "datetime")
    private DateTime _createdOn;

    // Use standard messages for each Notification Type
    public Notification(NotificationType type, User sender, User receiver, LocalDate requestStart, LocalDate requestEnd) throws NotificationException {
        this(type, getStandardMessage(type, sender), sender, receiver, requestStart, requestEnd);
    }

    public Notification(NotificationType type, String message, User sender, User receiver, LocalDate requestStart, LocalDate requestEnd) throws NotificationException {
        setType(type);
        setMessage(message);
        setSender(sender);
        setReceiver(receiver);
        setRequestedStartDate(requestStart);
        setRequestedEndDate(requestEnd);
        setRead(false);
        _createdOn = DateTime.now();
        _isVisible = true;
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

    public boolean isAccepted() {
        return _accepted;
    }

    public void setAccepted(boolean accepted) {
        this._accepted = accepted;
    }

    public static String getStandardMessage(NotificationType type, User sender) {
        return Messages.get("notifications." + type.name().toLowerCase(), sender.getFirstName() + " " + sender.getLastName());
    }

    public LocalDate getRequestedEndDate() {
        return _requestedEndDate;
    }

    public void setRequestedEndDate(LocalDate requestedEndDate) {
        this._requestedEndDate = requestedEndDate;
    }

    public LocalDate getRequestedStartDate() {
        return _requestedStartDate;
    }

    public void setRequestedStartDate(LocalDate requestedStartDate) {
        this._requestedStartDate = requestedStartDate;
    }

    public void makeInvisible() {
        _isVisible = false;
    }
}

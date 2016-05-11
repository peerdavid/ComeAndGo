package models;

import business.notification.NotificationException;
import business.notification.NotificationType;
import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by david on 22.03.16.
 */
@Entity
public class Notification extends Model {

    // DON'T rename column name id, otherwise limit functions won't work anymore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private NotificationType type;

    @Column(name = "message", columnDefinition = "varchar(150)")
    @Constraints.MaxLength(150)
    private String message;

    @Column(name = "_sender_id")
    @ManyToOne()
    @NotNull
    private User sender;

    @Column(name = "_receiver_id")
    @ManyToOne()
    @NotNull
    private User receiver;

    @Column(name = "seen")
    private Boolean read;

    @Column(name = "created", columnDefinition = "datetime")
    private DateTime createdOn;

    @Column(name = "reference_id")
    private Integer referenceId;


    public Notification(NotificationType type, User sender, User receiver) throws NotificationException {
        this(type, "", sender, receiver, 0);
    }

    public Notification(NotificationType type, User sender, User receiver, Integer referenceId) throws NotificationException {
        this(type, "", sender, receiver, referenceId);
    }

    public Notification(NotificationType type, String message, User sender, User receiver) throws NotificationException {
        this(type, message, sender, receiver, null);
    }

    public Notification(NotificationType type, String message, User sender, User receiver, Integer referenceId) throws NotificationException {
        setType(type);
        setMessage(message);
        setSender(sender);
        setReceiver(receiver);
        setRead(false);
        setReferenceId(referenceId);
        createdOn = DateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) throws NotificationException {
        if (message.length() > 150) {
            throw new NotificationException("exceptions.notification.message_length");
        }
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User user) throws NotificationException {
        this.sender = user;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User user) throws NotificationException {
        this.receiver = user;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }
}

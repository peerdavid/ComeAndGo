package models;

import business.timetracking.PayoutType;
import business.timetracking.RequestState;
import business.timetracking.TimeTrackException;
import com.avaje.ebean.annotation.Index;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by paz on 02.05.16.
 */
@Entity
public class Payout {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "_user_id")
    @NotNull
    @Index
    @ManyToOne()
    private User user;

    @Column(name = "start", columnDefinition = "datetime")
    private DateTime from;

    @Column(name = "end", columnDefinition = "datetime")
    private DateTime to;

    @Column(name = "type")
    private PayoutType type;

    @Column(name = "state")
    private RequestState state;

    @Column(name = "comment", columnDefinition = "varchar(150)")
    @Constraints.MaxLength(150)
    private String comment;

    @Column(name = "_reviewed_by")
    @ManyToOne
    private User reviewedBy;

    public Payout(User user, DateTime from, DateTime to, PayoutType type, RequestState state, String comment) throws TimeTrackException {
        setFrom(from);
        setTo(to);

        if(user == null) {
            throw new TimeTrackException("user is null");
        }
        if(type == null) {
            throw new TimeTrackException("type is null");
        }
        if(state == null) {
            throw new TimeTrackException("state is null");
        }

        this.user = user;
        this.type = type;
        this.state = state;
        this.comment = comment;
    }

    public void setFrom(DateTime from) throws TimeTrackException {
        if(to != null && from.isAfter(to)) {
            throw new TimeTrackException("from date is after to date");
        }
        this.from = from;
    }

    public void setTo(DateTime to) throws TimeTrackException {
        if(from != null && to.isBefore(from)) {
            throw new TimeTrackException("to date is before from date");
        }
        this.to = to;
    }

    public PayoutType getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public RequestState getState() {
        return state;
    }

    public String getComment() {
        return comment;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public void setType(PayoutType type) {
        this.type = type;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

}

package models;

import business.timetracking.*;
import com.avaje.ebean.annotation.Index;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by paz on 24.04.16.
 * Renamed SpecialDay to TimeOff
 */
@Entity
public class TimeOff {
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
    private TimeOffType type;

    @Column(name = "state")
    private TimeOffState state;

    @Column(name = "comment", columnDefinition = "varchar(150)")
    @Constraints.MaxLength(150)
    private String comment;

    @Column(name = "_reviewed_by")
    @ManyToOne
    private User reviewedBy;

    public TimeOff(User user, DateTime from, DateTime to, TimeOffType type, TimeOffState state, String comment) throws TimeTrackException {
        setFrom(from);
        setTo(to);

        if(user == null) {
            throw new TimeOffNotFoundException("user is null");
        }
        if(type == null) {
            throw new TimeOffNotFoundException("type is null");
        }
        if(state == null) {
            throw new TimeOffNotFoundException("state is null");
        }

        this.user = user;
        this.type = type;
        this.state = state;
        this.comment = comment;
    }

    public void setFrom(DateTime from) throws TimeOffInvalidDateException {
        if(to != null && from.isAfter(to)) {
            throw new TimeOffInvalidDateException("from date is after to date");
        }
        this.from = from;
    }

    public void setTo(DateTime to) throws TimeOffInvalidDateException {
        if(from != null && to.isBefore(from)) {
            throw new TimeOffInvalidDateException("to date is before from date");
        }
        this.to = to;
    }

    public TimeOffType getType() {
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

    public TimeOffState getState() {
        return state;
    }

    public String getComment() {
        return comment;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setState(TimeOffState state) {
        this.state = state;
    }

    public void setType(TimeOffType type) {
        this.type = type;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}

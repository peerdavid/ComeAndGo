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
    private Integer _id;

    @Column(name = "_user_id")
    @NotNull
    @Index
    @ManyToOne()
    private User _user;

    @Column(name = "start", columnDefinition = "datetime")
    private DateTime _from;

    @Column(name = "end", columnDefinition = "datetime")
    private DateTime _to;

    @Column(name = "type")
    private TimeOffType _type;

    @Column(name = "state")
    private TimeOffState _state;

    @Column(name = "comment", columnDefinition = "varchar(150)")
    @Constraints.MaxLength(150)
    private String _comment;

    @Column(name = "_reviewed_by")
    @ManyToOne
    private User _reviewedBy;

    public TimeOff(User user, DateTime from, DateTime to, TimeOffType type, TimeOffState state, String comment) throws TimeTrackException {
        setFrom(from);
        setTo(to);

        if(user == null) {
            throw new TimeOffNullPointerException("user is null");
        }
        if(type == null) {
            throw new TimeOffNullPointerException("type is null");
        }
        if(state == null) {
            throw new TimeOffNullPointerException("state is null");
        }

        this._user = user;
        this._type = type;
        this._state = state;
        this._comment = comment;
    }

    public void setFrom(DateTime from) throws TimeOffInvalidDateException {
        if(_to != null && from.isAfter(_to)) {
            throw new TimeOffInvalidDateException("from date is after to date");
        }
        this._from = from;
    }

    public void setTo(DateTime to) throws TimeOffInvalidDateException {
        if(_from != null && to.isBefore(_from)) {
            throw new TimeOffInvalidDateException("to date is before from date");
        }
        this._to = to;
    }

    public TimeOffType getType() {
        return _type;
    }

    public Integer getId() {
        return _id;
    }

    public User getUser() {
        return _user;
    }

    public DateTime getFrom() {
        return _from;
    }

    public DateTime getTo() {
        return _to;
    }

    public TimeOffState getState() {
        return _state;
    }

    public String getComment() {
        return _comment;
    }

    public User getReviewedBy() {
        return _reviewedBy;
    }

    public void setState(TimeOffState state) {
        this._state = state;
    }

    public void setType(TimeOffType type) {
        this._type = type;
    }

    public void setReviewedBy(User reviewedBy) {
        this._reviewedBy = reviewedBy;
    }
}

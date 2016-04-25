package models;

import business.timetracking.TimeOffState;
import business.timetracking.TimeOffType;
import com.avaje.ebean.annotation.Index;
import org.joda.time.DateTime;

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

    @Column(name = "user_id")
    @NotNull
    @Index
    @ManyToOne()
    private User _user;

    @Column(name = "start", columnDefinition = "time")
    private DateTime _from;

    @Column(name = "end", columnDefinition = "time")
    private DateTime _to;

    @Column(name = "type")
    private TimeOffType _type;

    @Column(name = "state")
    private TimeOffState _state;

    @Column(name = "comment")
    private String _comment;

    @Column(name = "reviewed_by")
    private User _reviewed_by;

    public TimeOff(User _user, DateTime _from, DateTime _to, TimeOffType _type, TimeOffState _state, String _comment) {
        this._user = _user;
        this._from = _from;
        this._to = _to;
        this._type = _type;
        this._state = _state;
        this._comment = _comment;
    }
}

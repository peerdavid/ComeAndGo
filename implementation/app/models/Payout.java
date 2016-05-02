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

    // Amount can be hours for Overtime or days for holiday
    @Column(name = "amount")
    int amount;

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

    public Payout(User user, PayoutType type, int amount, RequestState state, String comment) throws TimeTrackException {
        setAmount(amount);

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

    public void setAmount(int amount) throws TimeTrackException {
        if(amount < 0) {
            throw new TimeTrackException("Invalid amount of time");
        }
        this.amount = amount;
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

    public int getAmount() {
        return amount;
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

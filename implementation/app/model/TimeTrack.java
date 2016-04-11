package model;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Index;
import org.joda.time.DateTime;
import play.data.format.Formats;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by stefan on 07.04.16.
 */
@Entity
public class TimeTrack extends Model {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _id;

    @Column(name = "_user_id")
    @NotNull
    @Index
    @ManyToOne()
    private User _user;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Column(name = "start", columnDefinition = "datetime")
    private DateTime _from;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Column(name = "end", columnDefinition = "datetime")
    private DateTime _to;

    @OneToMany(cascade= CascadeType.ALL)
    @Column(name = "breaks")
    private List<Break> _breaks;

   /**
    * sets userId and from_time to the current date and time
    * @param user
    */
   public TimeTrack(User user) {
      _user = user;
      _from = DateTime.now();
   }

    public TimeTrack(int id, DateTime from, DateTime to, List<Break> breaks) {
        this._id = id;
        this._from = from;
        this._to = to;
        this._breaks = breaks;
    }


    public int get_id() {
        return _id;
    }


    public DateTime get_from() {
        return _from;
    }


    public void set_from(DateTime from) {
        if (_to != null && from.isAfter(_to)) {
            throw new InvalidParameterException("From time is after to time");
        }

        _from = from;
    }


    public DateTime get_to() {
        return _to;
    }


    public void set_to(DateTime to) {
        if (to.isBefore(_from)) {
            throw new InvalidParameterException("To time is before from time");
        }

        _to = to;
    }


    public List<Break> get_breaks() {
        return _breaks;
    }


    public void addBreak(Break timeBreak){
        this._breaks.add(timeBreak);
    }
}

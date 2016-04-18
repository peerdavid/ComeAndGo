package models;

import business.UserException;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Index;
import infrastructure.TimeTrackException;
import org.joda.time.DateTime;
import play.data.format.Formats;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    // initialize the following list to a new Arraylist, so that it will be returned an empty list in case of no breaks
    @OneToMany(cascade= CascadeType.ALL)
    @Column(name = "breaks")
    private List<Break> _breaks = new ArrayList();

   /**
    * sets userId and from_time to the current date and time
    * @param user
    */
   public TimeTrack(User user) {
      _user = user;
      _from = DateTime.now();
   }

    public TimeTrack(int id, DateTime from, DateTime to, List<Break> breaks) throws UserException {
        setFrom(from);
        setTo(to);
        this._breaks = breaks;
    }


    public int getId() {
        return _id;
    }


    public DateTime getFrom() {
        return _from;
    }


    public void setFrom(DateTime from) throws UserException {
        if (_to != null && from.isAfter(_to)) {
            throw new UserException("exceptions.timetracking.user_timetrack_error");
        }

        _from = from;
    }


    public DateTime getTo() {
        return _to;
    }


    public void setTo(DateTime to) throws UserException {
        if (to.isBefore(_from)) {
            throw new UserException("exceptions.timetracking.user_timetrack_error");
        }

        _to = to;
    }


    public List<Break> getBreaks() {
        return _breaks;
    }


    public void addBreak(Break breakToAdd){
        this._breaks.add(breakToAdd);
    }
}

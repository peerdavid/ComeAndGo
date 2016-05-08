package models;

import business.usermanagement.UserException;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Index;
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
    private Integer id;

    @Column(name = "_user_id")
    @NotNull
    @Index
    @ManyToOne(cascade = CascadeType.REFRESH)
    private User user;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Column(name = "start", columnDefinition = "datetime")
    private DateTime from;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    @Column(name = "end", columnDefinition = "datetime")
    private DateTime to;

    // initialize the following list to a new Arraylist, so that it will be returned an empty list in case of no breaks
    @Column(name = "breaks")
    @OneToMany(cascade= CascadeType.ALL)
    private List<Break> breaks = new ArrayList();

   /**
    * sets userId and from_time to the current date and time
    * @param user
    */
   public TimeTrack(User user) {
      this.user = user;
      from = DateTime.now();
   }

    public TimeTrack(User user, DateTime from, DateTime to, List<Break> breaks) throws UserException {
       this.user = user;
       setFrom(from);
       setTo(to);
       if(breaks != null) {      // if we get that case, breaks should keep an empty list (see above)
          this.breaks = breaks;
       }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public DateTime getFrom() {
        return from;
    }


    public void setFrom(DateTime from) throws UserException {
        if (to != null && from.isAfter(to)) {
            throw new UserException("exceptions.timetracking.user_timetrack_error");
        }

        this.from = from;
    }


    public DateTime getTo() {
        return to;
    }


    public void setTo(DateTime to) throws UserException {
        if (to.isBefore(from)) {
            throw new UserException("exceptions.timetracking.user_timetrack_error");
        }

        this.to = to;
    }


    public List<Break> getBreaks() {
        return breaks;
    }


    public void addBreak(Break breakToAdd){
        breaks.add(breakToAdd);
    }

    public User getUser() {
      return user;
   }

    public void setUser(User user) {
        this.user = user;
    }
}

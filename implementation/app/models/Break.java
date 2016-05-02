package models;

import business.usermanagement.UserException;
import com.avaje.ebean.Model;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by stefan on 07.04.16.
 */
@Entity
public class Break extends Model {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

   @Column(name = "start", columnDefinition = "time")
   private DateTime from;

   @Column(name = "end", columnDefinition = "time")
   private DateTime to;


    /**
     * @param from, to
     */
    public Break(DateTime from, DateTime to) throws UserException {
       setFrom(from);
       setTo(to);
    }

   /**
    * start break at "from" and end is meanwhile set to null
    * @param from
    */
    public Break(DateTime from) {
        this.from = from;
        to = null;
    }


    public int getId() {
        return id;
    }


    /**
     * this function returns ONLY correct TIME informations from database, dates are not valid
     * @return FROM time
     */
    public DateTime getFrom() {
        return from;
    }


    /**
     * only time information will be stored into db, date information will be ignored
     * @param from
     * @throws UserException
     */
    public void setFrom(DateTime from) throws UserException {
       if (this.to != null && from.isAfter(this.to)) {
          throw new UserException("exceptions.timetracking.user_break_error");
       }
       this.from = from;
    }


    /**
     * this function returns ONLY correct TIME informations from database, dates are not valid
     * @return TO time
     */
    public DateTime getTo() {
        return this.to;
    }


    /**
     * only time informations will be stored into database, date information will be ignored
     * @param to
     * @throws UserException
     */
    public void setTo(DateTime to) throws UserException {
       if(to.isBefore(this.from)) {
          throw new UserException("exceptions.timetracking.user_break_error");
       }
       this.to = to;
    }
}

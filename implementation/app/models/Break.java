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
    private int _id;

   @Column(name = "start", columnDefinition = "time")
   private DateTime _from;

   @Column(name = "end", columnDefinition = "time")
   private DateTime _to;


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
        _from = from;
        _to = null;
    }


    public int getId() {
        return _id;
    }


    /**
     * this function returns ONLY correct TIME informations from database, dates are not valid
     * @return FROM time
     */
    public DateTime getFrom() {
        return _from;
    }


    /**
     * only time information will be stored into db, date information will be ignored
     * @param from
     * @throws UserException
     */
    public void setFrom(DateTime from) throws UserException {
       if (_to != null && from.isAfter(_to)) {
          throw new UserException("exceptions.timetracking.user_break_error");
       }
       this._from = from;
    }


    /**
     * this function returns ONLY correct TIME informations from database, dates are not valid
     * @return TO time
     */
    public DateTime getTo() {
        return _to;
    }


    /**
     * only time informations will be stored into database, date information will be ignored
     * @param to
     * @throws UserException
     */
    public void setTo(DateTime to) throws UserException {
       if(to.isBefore(_from)) {
          throw new UserException("exceptions.timetracking.user_break_error");
       }
       _to = to;
    }
}

package model;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import play.data.format.Formats;

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

   @Formats.DateTime(pattern="yyyy-MM-dd")
   @Column(name = "start", columnDefinition = "datetime")
   private DateTime _from;

   @Formats.DateTime(pattern="yyyy-MM-dd")
   @Column(name = "end", columnDefinition = "datetime")
   private DateTime _to;


    /**
     * @param from, to
     */
    public Break(DateTime from, DateTime to) {
        setFrom(from);
        setTo(to);
    }

   /**
    * start break at "from" and end is meanwhile set to null
    * @param from
    */
    public Break(DateTime from) {
        setFrom(from);
        _to = null;
    }


    public int get_id() {
        return _id;
    }


    public DateTime getFrom() {
        return _from;
    }


    public void setFrom(DateTime from) {
        this._from = from;
    }


    public DateTime getTo() {
        return _to;
    }


    public void setTo(DateTime to) {
       if(to.isBefore(_from)) {
          throw new IllegalArgumentException("to time is before from time");
       }
       _to = to;
    }
}

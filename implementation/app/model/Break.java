package model;

import com.avaje.ebean.Model;
import org.joda.time.DateTime;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by stefan on 07.04.16.
 */
@Entity
public class Break extends Model {

    @Id
    @Column(name = "id")
    private int _id;

   @Formats.DateTime(pattern="dd/MM/yyyy")
   @Column(name = "from")
   private DateTime _from;

   @Formats.DateTime(pattern="dd/MM/yyyy")
   @Column(name = "to")
   private DateTime _to;


    /**
     * Default ctor for break.
     */
    public Break(int id, DateTime from, DateTime to) {
        _id = id;
        _from = from;
        _to = to;
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
        this._to = to;
    }
}

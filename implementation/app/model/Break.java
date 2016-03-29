package model;

import org.joda.time.DateTime;

/**
 * Created by david on 21.03.16.
 */
public class Break {

    private int _id;
    private DateTime _from;
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

package model;

import org.joda.time.DateTime;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public class TimeTrack {

    private int _id;
    private DateTime _from;
    private DateTime _to;
    private List<Break> _breaks;


    /**
     * If the Time track gets initially created, we don't need an id or anything else.
     * This will be filled by our repository (primary auto key)
     */
    public TimeTrack(){}

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


    public void set_from(DateTime _from){
        if(_from.isAfter(_to)){
            throw new InvalidParameterException("From time is after to time");
        }

        this._from = _from;
    }


    public DateTime get_to() {
        return _to;
    }


    public void set_to(DateTime _to){
        if(_to.isBefore(_from)){
            throw new InvalidParameterException("To time is before from time");
        }

        this._to = _to;
    }


    public List<Break> get_breaks() {
        return _breaks;
    }


    public void addBreak(Break timeBreak){
        this._breaks.add(timeBreak);
    }
}

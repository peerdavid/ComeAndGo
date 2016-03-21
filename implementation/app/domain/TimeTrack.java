package domain;

import org.joda.time.DateTime;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public class TimeTrack {

    private int id;
    private DateTime from;
    private DateTime to;
    private List<Break> breaks;


    /**
     * If the Time track gets initially created, we don't need an id or anything else.
     * This will be filled by our repository (primary auto key)
     */
    public TimeTrack(){}

    public TimeTrack(int id, DateTime from, DateTime to, List<Break> breaks) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.breaks = breaks;
    }


    public int getId() {
        return id;
    }


    public DateTime getFrom() {
        return from;
    }


    public void setFrom(DateTime from){
        if(from.isAfter(to)){
            throw new InvalidParameterException("From time is after to time");
        }

        this.from = from;
    }


    public DateTime getTo() {
        return to;
    }


    public void setTo(DateTime to){
        if(to.isBefore(from)){
            throw new InvalidParameterException("To time is before from time");
        }

        this.to = to;
    }


    public List<Break> getBreaks() {
        return breaks;
    }


    public void addBreak(Break timeBreak){
        this.breaks.add(timeBreak);
    }
}

package domain;

import org.joda.time.DateTime;

/**
 * Created by david on 21.03.16.
 */
public class Break {

    private int id;
    private DateTime from;
    private DateTime to;


    public Break(int id, DateTime from, DateTime to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }


    public int getId() {
        return id;
    }


    public DateTime getFrom() {
        return from;
    }


    public void setFrom(DateTime from) {
        this.from = from;
    }


    public DateTime getTo() {
        return to;
    }


    public void setTo(DateTime to) {
        this.to = to;
    }
}

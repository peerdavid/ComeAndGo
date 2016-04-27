package business.timetracking;

/**
 * Created by Stefan on 27.04.2016.
 */
public class TimeOffInvalidDateException extends TimeTrackException {
    public TimeOffInvalidDateException(String msg) {
        super(msg);
    }
}

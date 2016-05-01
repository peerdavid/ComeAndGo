package controllers.timeoff;

import business.timetracking.TimeOffState;
import business.timetracking.TimeOffType;
import models.TimeOff;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by david on 01.05.16.
 */
public class TimeOffToXmlConverter {

    public static String timeOffToXml(TimeOff timeOff){
        int id = 7;//timeOff.getId();
        String name = StringEscapeUtils.escapeXml10(timeOff.getType().toString());
        String comment = StringEscapeUtils.escapeXml10(timeOff.getComment());

        String from = StringEscapeUtils.escapeXml10(timeOff.getFrom().toString("yyyy-MM-dd"));
        String to = StringEscapeUtils.escapeXml10(timeOff.getTo().toString("yyyy-MM-dd"));

        String color = StringEscapeUtils.escapeXml10(timeOffToColor(timeOff));

        return  String.format("\t<event>\n" +
                "\t\t<id>%d</id>\n" +
                "\t\t<name>%s (%s)</name>\n" +
                "\t\t<startdate>%s</startdate>\n" +
                "\t\t<enddate>%s</enddate>\n" +
                "\t\t<starttime></starttime>\n" +
                "\t\t<endtime></endtime>\n" +
                "\t\t<color>%s</color>\n" +
                "\t\t<url>/notification</url>\n" +
                "\t</event>\n", id, name, comment, from, to, color);
    }

    private static String timeOffToColor(TimeOff timeOff){

        String grey = "#BDBDBD";
        String orange = "#FF5722";
        String green = "#4CAF50";
        String yellow = "#FFEB3B";
        String blue = "#2196F3";
        String purple = "#9C27B0";

        switch (timeOff.getState()){
            case NEW:
            case REQUEST_SENT:
                return grey;

            case REQUEST_REJECTED:
                return orange;
        }

        switch (timeOff.getType()){
            case HOLIDAY:
                return green;

            case BUSINESS_TRIP:
                return yellow;

            case EDUCATIONAL_LEAVE:
            case PARENTAL_LEAVE:
            case SPECIAL_HOLIDAY:
                return blue;

            case SICK_LEAVE:
                return purple;

            default:
        }

        return grey;
    }
}

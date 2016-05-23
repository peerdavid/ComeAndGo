package models;

/**
 * Created by david on 02.05.16.
 */
public class ReportEntry {

    private User user;
    private double hoursPerDay;
    private int numOfUsedHolidays;
    private double numOfUnusedHolidays;
    private int numOfSickDays;
    private long workMinutesShould;
    private long workMinutesIs;
    private long breakMinutes;
    private long holidayPayoutHours;
    private long overtimePayoutHours;
    private int workdaysOfReport;

    public ReportEntry(User user, double hoursPerDay, int numOfUsedHolidays, double numOfUnusedHolidays, int numOfSickDays,
                       long workMinutesShould, long workMinutesIs, long breakMinutes, long holidayPayoutHours,
                       long overtimePayoutHours, int workdaysOfReport) {
        this.user = user;
        this.hoursPerDay = hoursPerDay;
        this.numOfUsedHolidays = numOfUsedHolidays;
        this.numOfUnusedHolidays = numOfUnusedHolidays;
        this.numOfSickDays = numOfSickDays;
        this.workMinutesShould = workMinutesShould;
        this.workMinutesIs = workMinutesIs;
        this.breakMinutes = breakMinutes;
        this.holidayPayoutHours = holidayPayoutHours;
        this.overtimePayoutHours = overtimePayoutHours;
        this.workdaysOfReport = workdaysOfReport;
    }


    public User getUser() {
        return user;
    }

    public double getHoursPerDay() {
        return hoursPerDay;
    }

    public int getNumOfUsedHolidays() {
        return numOfUsedHolidays;
    }

    public double getNumOfUnusedHolidays() {
        return numOfUnusedHolidays;
    }

    public int getNumOfSickDays() {
        return numOfSickDays;
    }

    public long getWorkMinutesShould() {
        return workMinutesShould;
    }

    public long getWorkMinutesIs() {
        return workMinutesIs;
    }

    public long getWorkMinutesDifference(){
        return workMinutesIs - workMinutesShould;
    }

    public long getBreakMinutes() {
        return breakMinutes;
    }

    public long getHolidayPayoutHours() {
        return holidayPayoutHours;
    }

    public long getOvertimePayoutHours() {
        return overtimePayoutHours;
    }

    public int getWorkdaysOfReport() {
        return workdaysOfReport;
    }
}

package models;

/**
 * Created by david on 02.05.16.
 */
public class ReportEntry {

    private User user;
    private double hoursPerDay;
    private int numOfUsedHolidays;
    private int numOfUnusedHolidays;
    private int numOfSickDays;
    private long workMinutesShould;
    private long workMinutesIs;
    private long breakMinutes;
    private long workDaysRespected;

    public ReportEntry(User user, double hoursPerDay, int numOfUsedHolidays, int numOfUnusedHolidays, int numOfSickDays,
                       long workMinutesShould, long workMinutesIs, long breakMinutes, long workDaysRespected) {
        this.user = user;
        this.hoursPerDay = hoursPerDay;
        this.numOfUsedHolidays = numOfUsedHolidays;
        this.numOfUnusedHolidays = numOfUnusedHolidays;
        this.numOfSickDays = numOfSickDays;
        this.workMinutesShould = workMinutesShould;
        this.workMinutesIs = workMinutesIs;
        this.breakMinutes = breakMinutes;
        this.workDaysRespected = workDaysRespected;
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

    public int getNumOfUnusedHolidays() {
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

    public long getWorkDaysRespected() {
        return workDaysRespected;
    }
}

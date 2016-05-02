package models;

/**
 * Created by david on 02.05.16.
 */
public class ReportEntry {

    private User user;
    private double salary;
    private int numOfUsedHolidays;
    private int numOfUnusedHolidays;
    private int numOfSickDays;
    private int workHoursShould;
    private int workHoursIs;


    public ReportEntry(User user, double salary, int numOfUsedHolidays, int numOfUnusedHolidays, int numOfSickDays, int workHoursShould, int workHoursIs) {
        this.user = user;
        this.salary = salary;
        this.numOfUsedHolidays = numOfUsedHolidays;
        this.numOfUnusedHolidays = numOfUnusedHolidays;
        this.numOfSickDays = numOfSickDays;
        this.workHoursShould = workHoursShould;
        this.workHoursIs = workHoursIs;
    }


    public User getUser() {
        return user;
    }

    public double getSalary() {
        return salary;
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

    public int getWorkHoursShould() {
        return workHoursShould;
    }

    public int getWorkHoursIs() {
        return workHoursIs;
    }

    public int getWorkHoursDifference(){
        return workHoursShould - workHoursIs;
    }
}

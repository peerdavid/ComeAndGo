package business.reporting;


import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import business.usermanagement.SecurityRole;
import models.*;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by paz on 09.05.16.
 */
public class CollectiveAgreementImplTest {

    User _testUser;
    CollectiveAgreement _testee;
    List<TimeOff> _timeoffs;
    List<TimeTrack> _timetracks;
    List<WorkTimeAlert> _alert;
    DateTime _now;

    @Before
    public void SetUp() throws Exception {
        _testUser = mock(User.class);
        when(_testUser.getFirstName()).thenReturn("Klaus");
        when(_testUser.getLastName()).thenReturn("Kleber");
        when(_testUser.getHoursPerDay()).thenReturn(38.5 / 5);
        when(_testUser.getEntryDate()).thenReturn(new DateTime(2016, 1, 10, 0, 0));
        when(_testUser.getHolidays()).thenReturn(25);
        _testee = new CollectiveAgreementImpl();
        _timeoffs = new ArrayList<>();
        _timetracks = new ArrayList<>();
        _alert = new ArrayList<>();
        _now = DateTime.now();
    }

    @Test
    public void createUserReport_WithNoTimeTracksAndTimeOffs_ShouldSucceed() throws Exception {

        ReportEntry actual = _testee.createUserReport(_testUser, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, DateTime.now());

        Assert.assertEquals(0, actual.getNumOfSickDays());

        double expectedUnusedHoliday = DateTimeUtils.getAliquoteHolidayDays(_testUser.getEntryDate(), DateTime.now(), _testUser.getHolidays());
        Assert.assertEquals(expectedUnusedHoliday, actual.getNumOfUnusedHolidays(), 0.1);
        Assert.assertEquals(0, actual.getNumOfUsedHolidays());
        Assert.assertEquals(0, actual.getBreakMinutes());
        Assert.assertEquals(0, actual.getWorkMinutesIs());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now());
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());
    }

    @Test
    public void createUserReport_WithHoliday_ShouldSucceed() throws Exception {
        DateTime date1 = new DateTime(2016, 2, 1, 0, 0);
        DateTime date2 = new DateTime(2016, 2, 20, 0, 0);
        TimeOff holiday21days = new TimeOff(_testUser, date1, date2, TimeOffType.HOLIDAY, RequestState.REQUEST_ACCEPTED, "");
        _timeoffs.add(holiday21days);

        ReportEntry actual = _testee.createUserReport(_testUser, Collections.EMPTY_LIST, _timeoffs, Collections.EMPTY_LIST, DateTime.now());

        double expectedUnusedHoliday = DateTimeUtils.getAliquoteHolidayDays(_testUser.getEntryDate(), DateTime.now(), _testUser.getHolidays())
                - DateTimeUtils.getWorkdaysOfTimeInterval(date1, date2);

        Assert.assertEquals(expectedUnusedHoliday, actual.getNumOfUnusedHolidays(), 0.1);
        Assert.assertEquals(15, actual.getNumOfUsedHolidays());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * (
                DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now())
                - DateTimeUtils.getWorkdaysOfTimeInterval(_timeoffs.get(0).getFrom(), _timeoffs.get(0).getTo()));
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());
    }

    @Test
    public void createUserReport_With1DayWorkingWithoutBreaks_ShouldSucceed() throws Exception {
        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST, DateTime.now());

        Assert.assertEquals(0, actual.getBreakMinutes());

        int expectedWorkMinutesIs = (date2.getMinuteOfDay() - date1.getMinuteOfDay());
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());


    }

    @Test
    public void createUserReport_With1DayWorkingWith2Breaks_ShouldSucceed() throws Exception {
        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        List<Break> breaks = new ArrayList<>();
        breaks.add(new Break(new DateTime(2016, 1, 12, 9, 0), new DateTime(2016, 1, 12, 9, 15)));
        breaks.add(new Break(new DateTime(2016, 1, 12, 12, 0), new DateTime(2016, 1, 12, 12, 45)));

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, breaks);
        _timetracks.add(work1dayWithoutBreaks);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST, DateTime.now());

        Assert.assertEquals(60, actual.getBreakMinutes());

        int expectedWorkMinutesIs = date2.getMinuteOfDay() - date1.getMinuteOfDay() - 60;
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());

    }

    @Test
    public void createUserReport_With2DaysWorkingAnd1DaySpecialHoliday_ShouldSucceed() throws Exception {
        TimeOff specialHoliday = new TimeOff(_testUser, new DateTime(2016, 2, 1, 0, 0), new DateTime(2016, 2, 2, 0, 0), TimeOffType.SPECIAL_HOLIDAY, RequestState.REQUEST_ACCEPTED, "");
        _timeoffs.add(specialHoliday);

        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks);

        DateTime date3 = new DateTime(2016, 1, 13, 8, 1);
        DateTime date4 = new DateTime(2016, 1, 13, 17, 28);

        TimeTrack work1dayWithoutBreaks2 = new TimeTrack(_testUser, date3, date4, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks2);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST, DateTime.now());


        Assert.assertEquals(0, actual.getNumOfUsedHolidays());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * (
                DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now())
                        - DateTimeUtils.getWorkdaysOfTimeInterval(_timeoffs.get(0).getFrom(), _timeoffs.get(0).getTo()));
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());

        int expectedWorkMinutesIs = date2.getMinuteOfDay() - date1.getMinuteOfDay() + (date4.getMinuteOfDay() - date3.getMinuteOfDay());
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithUserExceededAnnualFlextimeTolerance_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithFlextimeExceedingForUser(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.flextime_saldo_over_specified_percent");
        Assert.assertEquals(WorkTimeAlert.Type.URGENT, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithUserNearlyExceededFlextimeTolerance_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithNearlyExceededFlextimeForUser(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.flextime_saldo_over_specified_percent");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithUserTooManyMinusHours_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithTooManyMinusHours(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.flextime_saldo_under_specified_percent");
        Assert.assertEquals(WorkTimeAlert.Type.URGENT, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithUserNearlyTooManyMinusHours_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithNearlyTooManyMinusHours(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.flextime_saldo_under_specified_percent");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithRegularlyBreakOverUser_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithBreakOverUse(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.user_overuses_breaks_regularly");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithRegularlyBreakUnderUse_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithBreakUnderUse(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.user_underuses_breaks_regularly");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createGeneralWorkTimeAlert_WithHolidayOverUse_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithHolidayOverConsumption(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.more_holiday_used_than_available");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.URGENT);
    }

    @Test
    public void createGeneralWorkTimeAlert_WithTooManyUnusedHoliday_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithTooManyUnusedHoliday(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.too_many_unused_holiday_available");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.WARNING);
    }

    @Test
    public void createGeneralWorkTimeAlert_WithTooManySickDays_ShouldResultInAlert() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createAnnualReportWithTooManySickDays(_testUser), _alert);
        testForStringMembers(_alert, "forbidden_worktime.user_has_many_sick_leaves");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.URGENT);
    }

    @Test
    public void createGeneralWorkTimeAlerts_WithValidReportAsInput_ShouldResultInEmptyAlertsList() throws Exception {
        _testee.createGeneralWorkTimeAlerts(ReportEntryFactory.createValidReport(_testUser), _alert);
        Assert.assertEquals(_alert.size(), 0);
    }

    @Test
    public void createDailyWorkTimeAlerts_WithWorkTimeExceeding_ShouldResultInAlert() throws Exception {
        _testee.createWorkHoursOfDayAlerts(_testUser, CollectiveConstants.MAX_HOURS_PER_DAY, 0.5, _now, _alert);
        testForStringMembers(_alert, "forbidden_worktime.user_exceeded_daily_worktime");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createDailyWorkTimeAlerts_WithTooLessBreakOver6hWorkTime_ShouldResultInAlert() throws Exception {
        _testee.createWorkHoursOfDayAlerts(_testUser, 8, 0.25, _now, _alert);
        testForStringMembers(_alert, "forbidden_worktime.user_underused_break_on_date");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createDailyWorkTimeAlerts_WithTooLessBreakUnder6hWorkTime_ShouldResultInEmptyAlertList() throws Exception {
        _testee.createWorkHoursOfDayAlerts(_testUser, 5, 0, _now, _alert);
        Assert.assertEquals(_alert.size(), 0);
    }

    @Test
    public void createDailyWorkTimeAlerts_WithValidRequest_ShouldResultInEmptyAlertList() throws Exception {
        _testee.createWorkHoursOfDayAlerts(_testUser, 8, 0.5, _now, _alert);
        Assert.assertEquals(0, _alert.size());
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WithWorkedOn6Days_ShouldResultInAlert() throws Exception {
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, DateTimeUtils.startOfWeek(_now),
                workTimeListNextDays(), _alert);
        testForStringMembers(_alert, "forbidden_worktime.too_many_workdays_per_week");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WorkedOnChristmasAndNewYearsEve_ShouldResultInAlert() throws Exception {
        List<Double> workHoursNextDays = new ArrayList<>();
        workHoursNextDays.add(8.);  // work time on christmas eve
        workHoursNextDays.add(0.);  // 25.12.
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(6.);  // new years eve
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        DateTime christmas = DateTimeUtils.endOfActualYear().minusDays(7);
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, christmas, workHoursNextDays, _alert);
        testForStringMembers(_alert, "forbidden_worktime.worked_on_both_christmas_and_newyear_eve");
        Assert.assertEquals(WorkTimeAlert.Type.WARNING, _alert.get(0).getType());
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WorkedOnChristmasButNotAtNewYearsEve_ShouldResultInEmptyAlert() throws Exception {
        List<Double> workHoursNextDays = new ArrayList<>();
        workHoursNextDays.add(8.);  // work time on christmas eve
        workHoursNextDays.add(0.);  // 25.12.
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);  // new years eve
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        DateTime christmas = DateTimeUtils.endOfActualYear().minusDays(7);
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, christmas, workHoursNextDays, _alert);
        Assert.assertEquals(_alert.size(), 0);
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WorkedOnNewYearsEveButNotOnChristmas_ShouldResultInEmptyAlert() throws Exception {
        List<Double> workHoursNextDays = new ArrayList<>();
        workHoursNextDays.add(0.);  // work time on christmas eve
        workHoursNextDays.add(0.);  // 25.12.
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(9.);  // new years eve
        workHoursNextDays.add(0.);
        workHoursNextDays.add(0.);
        DateTime christmas = DateTimeUtils.endOfActualYear().minusDays(7);
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, christmas, workHoursNextDays, _alert);
        Assert.assertEquals(_alert.size(), 0);
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WorkedLegalTimes_ShouldResultInEmptyAlertList() throws Exception {
        List<Double> workHoursNextDays = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            workHoursNextDays.add(0.);
        }
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, DateTimeUtils.startOfWeek(_now),
                workHoursNextDays, _alert);
        Assert.assertEquals(0, _alert.size());
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WithEmptyHoursNextDaysOrNull_ShouldReturn() throws Exception {
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, _now, null, _alert);
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, _now, new ArrayList<>(), _alert);
        Assert.assertEquals(0, _alert.size());
    }

    @Test
    public void createFreeTimeWorkdaysPerWeekAlerts_WithHoursWorkedListSmallerThan10_ShouldSucceed() throws Exception {
        List<Double> workedHours = new ArrayList<>();
        workedHours.add(3.);
        workedHours.add(5.);
        _testee.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(_testUser, DateTimeUtils.startOfWeek(_now),
                workedHours, _alert);
        Assert.assertEquals(0, _alert.size());
    }

    @Test
    public void createFreetimeAlerts_WithNoListGiven_ShouldReturnInformationAlert() throws Exception {
        _testee.createFreeTimeHoursOfDayAlerts(_testUser, _now, null, _alert);
        _testee.createFreeTimeHoursOfDayAlerts(_testUser, _now, Collections.emptyList(), _alert);
        testForStringMembers(_alert, "forbidden_worktime.error_in_checking_freetime_and_christmas_clause",
                "forbidden_worktime.error_in_checking_freetime_and_christmas_clause");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.INFORMATION);
        Assert.assertEquals(_alert.get(1).getType(), WorkTimeAlert.Type.INFORMATION);
    }

    @Test
    public void createFreeTimeAlerts_WithTooLessFreeTimeNextDays_ShouldResultInAlert() throws Exception {
        List<Double> workTimeListNextDays = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            workTimeListNextDays.add(15.);
        }
        _testee.createFreeTimeHoursOfDayAlerts(_testUser, _now, workTimeListNextDays, _alert);
        testForStringMembers(_alert, "forbidden_worktime.freetime_undershoot_with_next_days_no_balance");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.WARNING);
    }

    @Test
    public void createFreeTimeAlerts_WithTooLessFreeTimeOnActualDay_ShouldResultInAlert() throws Exception {
        _testee.createFreeTimeHoursOfDayAlerts(_testUser, _now, workTimeListNextDays(), _alert);
        testForStringMembers(_alert, "forbidden_worktime.freetime_undershoot");
        Assert.assertEquals(_alert.get(0).getType(), WorkTimeAlert.Type.WARNING);
    }

    @Test
    public void createFreeTimeAlerts_WithValidUserWorkTimes_ShouldResultInEmptyList() throws Exception {
        List<Double> doubleList = new ArrayList<>();
        for(int i = 0; i < 10; ++i) {
            doubleList.add(0.);
        }
        _testee.createFreeTimeHoursOfDayAlerts(_testUser, _now, doubleList, _alert);
        Assert.assertEquals(0, _alert.size());
    }

    private List<Double> workTimeListNextDays() {
        List<Double> workHoursNextDays = new ArrayList<>();
        workHoursNextDays.add(15.);
        workHoursNextDays.add(10.);
        workHoursNextDays.add(0.);
        workHoursNextDays.add(9.);
        workHoursNextDays.add(10.);
        workHoursNextDays.add(15.);
        workHoursNextDays.add(13.);
        workHoursNextDays.add(8.);
        workHoursNextDays.add(7.);
        workHoursNextDays.add(7.);
        return workHoursNextDays;
    }

    private void testForStringMembers(List<WorkTimeAlert> alertList, String... expectedMembers) throws Exception {
        if(alertList.size() != expectedMembers.length)
            throw new Exception("not the same amount of members in alertList (" + alertList.size()
                    + ") and expectedMembers (" + expectedMembers.length + ")");

        List<String> alertStrings = new ArrayList<>();
        alertList.forEach(alert -> alertStrings.add(alert.getMessage()));

        for(String member : expectedMembers) {
            if(!alertStrings.contains(member))
                throw new Exception("expected member not found in alertList \"" + member + "\"");
        }
    }
}

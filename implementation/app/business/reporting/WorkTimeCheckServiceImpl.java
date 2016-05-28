package business.reporting;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Report;
import models.ReportEntry;
import models.User;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Stefan on 23.05.2016.
 */
class WorkTimeCheckServiceImpl implements WorkTimeCheckService {
    private ReportingService _reporting;
    private CollectiveAgreement _collectiveAgreement;
    private InternalUserManagement _userManagement;

    @Inject
    public WorkTimeCheckServiceImpl(ReportingService reporting, CollectiveAgreement collectiveAgreement, InternalUserManagement userManagement) {
        _reporting = reporting;
        _collectiveAgreement = collectiveAgreement;
        _userManagement = userManagement;
    }

    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, final DateTime from, final DateTime to, int actualUserId) throws Exception {
        validateDate(from, to);

        List<WorkTimeAlert> alertList = new ArrayList<>();
        userList.forEach(user -> {
            final DateTime start = (from == null || from.isBefore(user.getEntryDate())) ? user.getEntryDate() : from;
            final DateTime end = (to == null) ? DateTime.now() : to;
            try {
                alertList.addAll(readForbiddenWorkTimeAlerts(user.getId(), start, end, actualUserId));
            } catch (Exception e) {
                alertList.add(new WorkTimeAlert("forbidden_worktime.error_in_reading_alerts_from_user",
                        WorkTimeAlert.Type.INFORMATION, user.getFirstName() + " " + user.getLastName()));
            }
        });
        return alertList;
    }

    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime from, DateTime to, int actualUserId) throws Exception {
        validateDate(from, to);
        validateUser(userId, actualUserId);

        DateTime entryDate = _userManagement.readUser(userId).getEntryDate();
        from = (from == null || from.isBefore(entryDate)) ? entryDate : from;
        to = (to == null) ? DateTime.now() : to;

        Report report = _reporting.createEmployeeReport(userId, from, to);
        return readForbiddenWorkTimeAlerts(report.getUserReports().get(0), from, to);
    }

    private List<WorkTimeAlert> readForbiddenWorkTimeAlerts(ReportEntry entry, DateTime from, DateTime to) throws Exception {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        User user = entry.getUser();

        // standard alerts
        _collectiveAgreement.createGeneralWorkTimeAlerts(entry, alertList);

        // check for alerts which have to be observed daily (overtime, ...)
        for(DateTime actualDate = from; actualDate.isBefore(to); actualDate = actualDate.plusDays(1)) {
            double hoursWorked = _reporting.readHoursWorked(user.getId(), actualDate);

            List<Double> hoursWorkedNextDays = new ArrayList<>();
            hoursWorkedNextDays.add(hoursWorked);
            for(int i = 1; i <= 10; ++i) {
                hoursWorkedNextDays.add(_reporting.readHoursWorked(user.getId(), actualDate.plusDays(i)));
            }
            _collectiveAgreement.createWorkHoursOfDayAlerts(user, hoursWorked, actualDate, alertList);
            _collectiveAgreement.createFreeTimeHoursOfDayAlerts(user, actualDate, hoursWorkedNextDays, alertList);
            _collectiveAgreement.createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(user, actualDate, hoursWorkedNextDays, alertList);
        }
        Collections.sort(alertList);
        return alertList;
    }

    private void validateDate(DateTime from, DateTime to) throws UserException {
        if(from != null && to != null && from.isAfter(to)) {
            throw new UserException("exceptions.forbidden_worktime.error_in_date_from_or_to");
        }
    }

    private void validateUser(int userIdRequested, int userIdActual) throws UserException {
        // if user requests his own workTimeAlerts
        if(userIdRequested == userIdActual) {
            return;
        }
        // if requester is personal manager he is allowed to see all alerts
        User user = _userManagement.readUser(userIdActual);
        if(user.getRole().equals(SecurityRole.ROLE_PERSONNEL_MANAGER)) {
            return;
        }
        // if requester is in any way a boss of requested employee he is allowed to see alerts
        if(isABossOfUser(userIdRequested, userIdActual)) {
            return;
        }
        throw new UserException("exceptions.forbidden_worktime.no_permission_to_read");
    }

    private boolean isABossOfUser(int userId, int potentialBossId) throws UserException {
        boolean isBoss = false;
        User requestedUser = _userManagement.readUser(userId);
        User bossOfUser = requestedUser.getBoss();
        while(requestedUser.getId() != bossOfUser.getId()) {
            if(bossOfUser.getId() == potentialBossId) {
                isBoss = true;
                break;
            }
            requestedUser = bossOfUser;
            bossOfUser = requestedUser.getBoss();
        }
        return isBoss;
    }
}

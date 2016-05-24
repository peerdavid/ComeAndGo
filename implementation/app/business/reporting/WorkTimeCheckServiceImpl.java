package business.reporting;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Report;
import models.ReportEntry;
import models.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import utils.DateTimeUtils;

import java.util.ArrayList;
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
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime from, DateTime to, int actualUserId) throws Exception {
        validateDate(from, to);

        List<WorkTimeAlert> alertList = new ArrayList<>();
        for(User user : userList) {
            from = (from == null) ? user.getEntryDate() : from;
            to = (to == null) ? DateTime.now() : to;

            alertList.addAll(readForbiddenWorkTimeAlerts(user.getId(), from, to, actualUserId));
        }
        return alertList;
    }

    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime from, DateTime to, int actualUserId) throws Exception {
        validateDate(from, to);
        //validateUser(userId, actualUserId);

        from = (from == null) ? _userManagement.readUser(userId).getEntryDate() : from;
        to = (to == null) ? DateTime.now() : to;

        Report report = _reporting.createEmployeeReport(userId, from, to);
        return readForbiddenWorkTimeAlerts(report.getUserReports().get(0), from, to);
    }

    private List<WorkTimeAlert> readForbiddenWorkTimeAlerts(ReportEntry entry, DateTime from, DateTime to) throws Exception {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        User user = entry.getUser();

        // check for standard alerts
        _collectiveAgreement.createForbiddenWorkTimeAlerts(entry, alertList);

        // check for exceeded work time per day
        DateTime startDate = user.getEntryDate().isBefore(from) ? from : user.getEntryDate();
        for(DateTime actualDate = startDate; actualDate.isBefore(to); actualDate = actualDate.plusDays(1)) {
            double hoursWorked = _reporting.readHoursWorked(user.getId(), actualDate);
            _collectiveAgreement.checkWorkHoursOfDay(user, hoursWorked, actualDate, alertList);

            DateTime startDay = actualDate;
            List<Double> hoursWorkedNextDays = new ArrayList<>();
            for(int i = 1; i <= 10; ++i) {
                hoursWorkedNextDays.add(_reporting.readHoursWorked(user.getId(), startDay.plusDays(i)));
            }
            _collectiveAgreement.checkFreeTimeHoursOfDay(user, actualDate,
                    DateTimeConstants.HOURS_PER_DAY - hoursWorked, hoursWorkedNextDays, alertList);
            _collectiveAgreement.checkFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClause(user, actualDate,
                    hoursWorked, hoursWorkedNextDays, alertList);
        }
        return alertList;
    }

    private void validateDate(DateTime from, DateTime to) throws UserException {
        if(from != null && to != null && from.isAfter(to)) {
            throw new UserException("forbidden_worktime.error_in_date_from_or_to");
        }
    }

    private void validateUser(int userIdRequested, int userIdActual) throws UserException {
        // if user requests his own workTimeAlerts
        if(userIdRequested == userIdActual) {
            return;
        }
        // if requester is personnell manager he is allowed to see all alerts
        User user = _userManagement.readUser(userIdActual);
        if(user.getRole().equals("")) {
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
            }
            requestedUser = bossOfUser;
            bossOfUser = requestedUser.getBoss();
        }
        return isBoss;
    }
}

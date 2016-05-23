package business.reporting;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import com.google.inject.Inject;
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
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId) throws Exception {
        List<User> userList = new ArrayList<>();
        User user = _userManagement.readUser(userId);
        userList.add(user);
        return readForbiddenWorkTimeAlerts(userList, user.getEntryDate(), DateTime.now());
    }

    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception {
        List<User> userList = new ArrayList<>();
        User user = _userManagement.readUser(userId);
        userList.add(user);
        return readForbiddenWorkTimeAlerts(userId, DateTimeUtils.BIG_BANG, to);
    }

    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime from, DateTime to) throws Exception {
        if(from.isAfter(to)) {
            throw new UserException("");
        }
        List<WorkTimeAlert> alertList = new ArrayList<>();

        // TODO: add connection to method in _reporting created by paz, which gives me the difference of two reports

            // at this point we have the report starting at "from" and ending at "to"
            //alertList.addAll(readForbiddenWorkTimeAlerts(null, from, to));


        return alertList;
    }

    private List<WorkTimeAlert> readForbiddenWorkTimeAlerts(ReportEntry entry, DateTime from, DateTime to) throws Exception {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        User user = entry.getUser();

        // check for standard alerts
        alertList.addAll(_collectiveAgreement.createForbiddenWorkTimeAlerts(entry));

        // check for exceeded work time per day
        DateTime actualDate = user.getEntryDate().isBefore(from) ? from : user.getEntryDate();
        while(actualDate.isBefore(to)) {
            double hoursWorked = _reporting.readHoursWorked(user.getId(), actualDate);
            alertList.addAll(_collectiveAgreement.checkWorkHoursOfDay(user, hoursWorked, actualDate));

            DateTime startDay = actualDate;
            List<Double> hoursWorkedNextDays = new ArrayList<>();
            for(int i = 1; i <= 10; ++i) {
                hoursWorkedNextDays.add(_reporting.readHoursWorked(user.getId(), startDay.plusDays(i)));
            }
            alertList.addAll(_collectiveAgreement.checkFreeTimeHoursOfDay(user, actualDate,
                    DateTimeConstants.HOURS_PER_DAY - hoursWorked, hoursWorkedNextDays));
            alertList.addAll(_collectiveAgreement.checkFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClause(user, actualDate,
                    hoursWorked, hoursWorkedNextDays));
            actualDate = actualDate.plusDays(1);
        }
        return alertList;
    }





}

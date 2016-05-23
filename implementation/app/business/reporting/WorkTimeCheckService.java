package business.reporting;

import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Stefan on 23.05.2016.
 */
interface WorkTimeCheckService {
    List<WorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime from, DateTime to) throws Exception;
    List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId) throws Exception;
    List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception;
}

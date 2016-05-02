package business.reporting;

import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.ReportEntry;
import models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class ReportingServiceImpl implements ReportingService {

    private UserManagement _userManagement;

    @Inject
    public ReportingServiceImpl(UserManagement userManagement){

        _userManagement = userManagement;
    }


    @Override
    public List<ReportEntry> getCompanyReport() throws Exception {
        List<ReportEntry> _report = new ArrayList<>();

        for(User user : _userManagement.readUsers()){
            _report.add(new ReportEntry(user, 1300.23, 10, 20, 7, 130, 120));
        }

        return _report;
    }
}

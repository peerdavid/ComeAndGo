package controllers;

import business.UserException;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by Leonhard on 13.04.2016.
 */
public class UserManagementController extends UserProfileController{

    private UserManagement _userManagement;

    @Inject
    public UserManagementController(UserManagement userManagement){
        _userManagement=userManagement;
    }

    @RequiresAuthentication(clientName = "default")
    public Result editUser() throws Exception{
        CommonProfile profile = getUserProfile();

        return ok(views.html.edituser.render(profile,_userManagement.getAllUsers()));
    }
}

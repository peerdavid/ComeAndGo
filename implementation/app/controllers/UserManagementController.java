package controllers;

import business.usermanagement.UserException;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;


import static play.mvc.Results.ok;

/**
 * Created by Leonhard on 13.04.2016.
 */
public class UserManagementController extends UserProfileController {

    public static final Form<User> FORM = Form.form(User.class);
    private UserManagement _userManagement;

    @Inject
    public UserManagementController(UserManagement userManagement) {
        _userManagement = userManagement;
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result readUsers() throws Exception {
        CommonProfile profile = getUserProfile();

        return ok(views.html.users.render(profile, _userManagement.readUsers()));
    }


    /*
    Username remains unchangable because the authenticator
     */
    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result updateUser() throws Exception {
        CommonProfile profile = getUserProfile();

        Form<User> form = FORM.bindFromRequest();

        String userName = form.data().get("username");
        String firstName = form.data().get("firstname");
        String lastName = form.data().get("lastname");
        String email = form.data().get("email");
        String password = form.data().get("password");
        String repeatPassword = form.data().get("repeat_password");
        String userNameBoss = form.data().get("boss");
        String role = form.data().get("role");
        double hoursPerDay = Double.parseDouble(form.data().get("hours-per-day"));

        // Password check
        if(password != null && !password.equals(repeatPassword)) {
            throw new UserException("exceptions.usermanagement.repeat_password_different");
        }

        // Set new user settings
        User changingUser = _userManagement.readUser(userName);
        changingUser.setFirstName(firstName);
        changingUser.setLastName(lastName);
        changingUser.setEmail(email);
        changingUser.setHoursPerDay(hoursPerDay);

        if (password != null && !password.isEmpty()) {
            changingUser.setPassword(password);
        }
        if (userNameBoss != null) {
            User boss = _userManagement.readUser(userNameBoss);
            changingUser.setBoss(boss);
        }
        if (role != null) {
            changingUser.setRole(role);
        }

        _userManagement.updateUser(changingUser.getUsername(), changingUser);

        return redirect(routes.UserManagementController.readUsers());
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result deleteUser(String userName) throws Exception {
//        CommonProfile profile = getUserProfile();

        if (userName.isEmpty()) {
            throw new Exception("Empty Username");
        }

        _userManagement.deleteUser(userName);

        return redirect(routes.UserManagementController.readUsers());
    }
}

package controllers;

import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

import java.util.List;


import static play.mvc.Results.ok;

/**
 * Created by Leonhard on 13.04.2016.
 */
public class UserManagementController extends UserProfileController {

    private UserManagement _userManagement;
    public static final Form<User> FORM = Form.form(User.class);

    @Inject
    public UserManagementController(UserManagement userManagement) {
        _userManagement = userManagement;
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result indexEditUser() throws Exception {
        CommonProfile profile = getUserProfile();

        return ok(views.html.edituser.render(profile, _userManagement.readUsers()));
    }


    /*
    Username remains unchangable because the authenticator
     */
    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result editUser() throws Exception {
        CommonProfile profile = getUserProfile();

        List<User> userList = _userManagement.readUsers();

        Form<User> form = FORM.bindFromRequest();

        int userId = Integer.parseInt(form.data().get("id"));
        String userName = form.data().get("username");
        String firstName = form.data().get("firstname");
        String lastName = form.data().get("lastname");
        String email = form.data().get("email");
        String password = form.data().get("password");
        String repeatPassword = form.data().get("repeat_password");
        String userNameBoss = form.data().get("boss");
        String role = form.data().get("role");
        User boss = _userManagement.readUser(userNameBoss);

        User changingUser = null;

        for (User u : userList) {
            if (u.getId() == userId) {
                changingUser = u;
                break;
            }
        }
        // should we edit userName?
        if (userName != null) {
            changingUser.setUserName(userName);
        }
        if (firstName != null) {
            changingUser.setFirstName(firstName);
        }
        if (lastName != null) {
            changingUser.setLastName(lastName);
        }
        if (email != null) {
            changingUser.setEmail(email);
        }
        if (password != null && repeatPassword != null && (!password.isEmpty()) && (!repeatPassword.isEmpty()) && password.equals(repeatPassword)) {
            changingUser.setPassword(password);
        }
        if (userNameBoss != null) {
            changingUser.set_boss(boss);
        }
        if (role != null) {
            changingUser.setRole(role);
        }

        _userManagement.updateUser(changingUser.getUserName(), changingUser);

        return ok(views.html.edituser.render(profile, _userManagement.readUsers()));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result deleteUser(String userName) throws Exception {
        CommonProfile profile = getUserProfile();

        if (userName.isEmpty()) {
            throw new Exception("Empty Username");
        }

        _userManagement.deleteUser(userName);

        return redirect(routes.UserManagementController.indexEditUser());
    }
}

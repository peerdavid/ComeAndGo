package business.usermanagement;

import business.UserException;
import com.google.inject.Inject;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
class UserManagementFacade implements UserManagement {


    private UserService _userService;

    @Inject
    public UserManagementFacade(UserService userService) {

        _userService = userService;
    }


    @Override
    public void registerUser(User userToRegister) throws UserException {

        _userService.registerNewUser(userToRegister);
    }


    @Override
    public void changeUserData(String userName, User newUserData) throws UserException {
        _userService.changeUser(userName, newUserData);

    }

    @Override
    public void deleteUser(String userToDelete) throws UserException {
        _userService.deleteUser(userToDelete);
    }

    @Override
    public User getUserData(String userName) throws UserException {
        return _userService.readUser(userName);
    }

    @Override
    public List<User> getAllUsers() throws UserException {
        return _userService.getListOfUsers();
    }

    @Override
    public boolean checkUserCredentials(String userName, String password) throws UserException {
        return _userService.checkUserCredentials(userName, password);
    }
}

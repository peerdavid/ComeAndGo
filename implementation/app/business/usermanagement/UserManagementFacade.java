package business.usermanagement;

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
    public void createUser(User userToRegister) throws UserException {
        _userService.createUser(userToRegister);
    }


    @Override
    public void updateUser(String userName, User newUserData) throws UserException {
        _userService.updateUser(userName, newUserData);

    }

    @Override
    public void deleteUser(String userToDelete) throws UserException {
        _userService.deleteUser(userToDelete);
    }

    @Override
    public User readUser(String userName) throws UserException {
        return _userService.readUser(userName);
    }

    @Override
    public List<User> readUsers() throws UserException {
        return _userService.readUsers();
    }

    @Override
    public boolean checkUserCredentials(String userName, String password) throws UserException {
        return _userService.checkUserCredentials(userName, password);
    }
}

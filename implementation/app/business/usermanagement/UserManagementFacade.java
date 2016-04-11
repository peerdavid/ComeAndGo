package business.usermanagement;

import business.UserException;
import com.google.inject.Inject;
import model.User;

/**
 * Created by david on 29.03.16.
 */
class UserManagementFacade implements UserManagement {


    private AuthenticatorService _authenticatorService;

    @Inject
    public UserManagementFacade(AuthenticatorService authenticatorService) {

        _authenticatorService = authenticatorService;
    }


    @Override
    public void registerUser(User userToRegister) throws UserException {

        _authenticatorService.registerNewUser(userToRegister);
    }


    @Override
    public void changeUserData(String userName, User newUserData) throws UserException {

    }

    @Override
    public void deleteUser(String userToDelete) throws UserException {
        _authenticatorService.deleteUser(userToDelete);
    }

    @Override
    public User getUserData(String userName) throws UserException {
        return _authenticatorService.readUser(userName);
    }

    @Override
    public boolean checkUserCredentials(String userName, String password) throws UserException {
        return _authenticatorService.checkUserCredentials(userName, password);
    }
}

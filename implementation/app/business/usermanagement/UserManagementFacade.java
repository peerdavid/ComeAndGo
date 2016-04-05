package business.usermanagement;

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
    public void registerUser(String userName, String password, String role,
                             String firstName, String lastName, String email) throws Exception {

        _authenticatorService.registerNewUser(userName, password, role, firstName, lastName, email);
    }


    @Override
    public void changeUserData(String userName, User newUserData) throws Exception {

    }

    @Override
    public void deleteUser(String userName) throws Exception {

    }

    @Override
    public User getUserData(String userName) {
        return null;
    }

    @Override
    public boolean checkUserCredentials(String userName, String password) {
        return _authenticatorService.checkUserCredentials(userName, password);
    }
}

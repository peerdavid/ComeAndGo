package business.usermanagement;

import com.google.inject.Inject;

/**
 * Created by david on 29.03.16.
 */
class UserManagementFacade implements UserManagement {


    private AuthenticatorService _authenticatorService;

    @Inject
    public UserManagementFacade(AuthenticatorService authenticatorService){

        _authenticatorService = authenticatorService;
    }


    @Override
    public void registerUser(String userName, String password, String role,
                             String firstName, String lastName) throws Exception {

        _authenticatorService.registerNewUser(userName, password, role, firstName, lastName);
    }
}

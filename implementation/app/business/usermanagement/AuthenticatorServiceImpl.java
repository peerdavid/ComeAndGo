package business.usermanagement;

import com.google.inject.Inject;
import infrastructure.UserRepository;
import model.User;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;

/**
 * Created by david on 03.04.16.
 */
class AuthenticatorServiceImpl implements AuthenticatorService{


    private UserRepository _userRepository;

    @Inject
    public AuthenticatorServiceImpl(UserRepository userRepository){

        _userRepository = userRepository;
    }

    @Override
    public User readUser(String userName) {
        return _userRepository.readUser(userName);
    }

    @Override
    public boolean checkUserCredentials(User user, String password) {
        return user != null && user.getPassword() == password;
    }

    @Override
    public void registerNewUser(String userName, String password, String role,
                                String firstName, String lastName) throws Exception {

        if(userAlreadyExists(userName)){
            throw new Exception("User already exist.");
        }

        User newUser = new User(userName, password, role, firstName, lastName, true);
        _userRepository.createUser(newUser);

    }

    private boolean userAlreadyExists(String userName){
        User aleadyExistingUser = _userRepository.readUser(userName);
        return aleadyExistingUser != null;
    }


    /**
     * ToDo.: i18n
     * @param credentials
     */
    @Override
    public void validate(UsernamePasswordCredentials credentials) {
        if (credentials == null) {
            throwsException("No credentials.");
        }

        String enteredUserName = credentials.getUsername();
        String enteredPassword = credentials.getPassword();
        User possibleUser = readUser(enteredUserName);

        if(checkUserCredentials(possibleUser, enteredPassword)){
            throwsException("Invalid credentials");
        }

        HttpProfile userProfile = getCookieForUser(possibleUser);
        credentials.setUserProfile(userProfile);
    }


    @NotNull
    private HttpProfile getCookieForUser(User possibleUser) {
        HttpProfile userProfile = new HttpProfile();
        userProfile.setId(possibleUser.getId());
        userProfile.addRole(possibleUser.getRole());
        userProfile.addAttribute("username", possibleUser.getUserName());
        userProfile.addPermission(possibleUser.getRole());

        return userProfile;
    }


    protected void throwsException(final String message) {
        throw new CredentialsException(message);
    }
}

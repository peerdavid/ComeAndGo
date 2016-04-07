package business.usermanagement;

import com.google.inject.Inject;
import infrastructure.UserRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import model.User;
import org.hibernate.validator.internal.util.logging.Log;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import play.data.validation.Constraints;
import play.i18n.Messages;

import java.util.NoSuchElementException;

/**
 * Created by david on 03.04.16.
 */
class AuthenticatorServiceImpl implements AuthenticatorService {


    private UserRepository _userRepository;

    @Inject
    public AuthenticatorServiceImpl(UserRepository userRepository) {
        _userRepository = userRepository;
    }

    @Override
    public User readUser(String userName) throws UserException {
        return _userRepository.readUser(userName);
    }

    @Override
    public boolean checkUserCredentials(String userName, String passwordCandidate) throws UserException {
        User userToCheck = readUser(userName);
        String hashedPassword = userToCheck.getPassword();
        return userToCheck.checkPassword(passwordCandidate, hashedPassword);
    }

    @Override
    public void registerNewUser(User newUser) throws UserException {

        // Input validation
        if (userAlreadyExists(newUser.getUserName())) {
            throw new UserException(Messages.get("exceptions.usermanagement.user_already_exists"));
        }

        _userRepository.createUser(newUser);

    }

    private boolean userAlreadyExists(String userName) {
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
            throwsException(Messages.get("exceptions.usermanagement.no_credentials"));
        }

        String enteredUserName = credentials.getUsername();
        String enteredPassword = credentials.getPassword();

        try {
            User possibleUser = readUser(enteredUserName);

            if (!checkUserCredentials(enteredUserName, enteredPassword)) {
                throwsException(Messages.get("exceptions.usermanagement.invalid_credentials"));
            }

            HttpProfile userProfile = getProfileForUser(possibleUser);
            credentials.setUserProfile(userProfile);
        } catch (UserException e) {
            // ToDo: what to do with UserException?
            e.printStackTrace();
        }

    }


    @NotNull
    private HttpProfile getProfileForUser(User possibleUser) {
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

package business.usermanagement;

import business.UserException;
import com.google.inject.Inject;
import infrastructure.UserRepository;
import model.User;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import play.i18n.Messages;

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
        User usertoRead = _userRepository.readUser(userName);
        if (usertoRead == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        return usertoRead;
    }

    @Override
    public boolean checkUserCredentials(String userName, String passwordCandidate) throws UserException {
        User userToCheck = readUser(userName);
        String hashedPassword = userToCheck.getPassword();
        return userToCheck.checkPassword(passwordCandidate, hashedPassword);
    }

    @Override
    /*
    ToDo: Check if a boss is set!
     */
    public void registerNewUser(User newUser) throws UserException {

        // Input validation
        if (userAlreadyExists(newUser.getUserName())) {
            throw new UserException("exceptions.usermanagement.user_already_exists");
        }

        if (_userRepository.readUser(newUser.getUserNameBoss()) == null) {
            throw new UserException("exceptions.usermanagement.invalid_boss");
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
            throwsException("exceptions.usermanagement.no_credentials");
        }

        String enteredUserName = credentials.getUsername();
        String enteredPassword = credentials.getPassword();

        try {
            User possibleUser = readUser(enteredUserName);

            if (!checkUserCredentials(enteredUserName, enteredPassword)) {
                throwsException("exceptions.usermanagement.invalid_credentials");
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

        userProfile.addAttribute("first_name", possibleUser.getFirstName());
        userProfile.addAttribute("family_name", possibleUser.getLastName());

        return userProfile;
    }

    @Override
    public void changeUser(String userName, User newUserData) throws UserException {
        User userToChange = _userRepository.readUser(userName);

        if (userToChange == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        if (userToChange.getId() != newUserData.getId()) {
            throw new UserException("exceptions.usermanagement.different_ids");
        }

        _userRepository.updateUser(newUserData);

    }

    @Override
    public void deleteUser(String userToDelete) throws UserException {
        if (_userRepository.readUser(userToDelete) == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        _userRepository.deleteUser(userToDelete);
    }

    protected void throwsException(final String message) {
        throw new CredentialsException(message);
    }
}

package business.usermanagement;

import business.UserException;
import com.google.inject.Inject;
import infrastructure.UserRepository;
import models.User;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;

import java.util.List;

/**
 * Created by david on 03.04.16.
 */
class UserServiceImpl implements UserService {


    private UserRepository _userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
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
    public List<User> readUsers() throws UserException {
        return _userRepository.readUsers();
    }

    @Override
    public boolean checkUserCredentials(String userName, String passwordCandidate) throws UserException {
        User userToCheck = readUser(userName);
        String hashedPassword = userToCheck.getPassword();
        return userToCheck.checkPassword(passwordCandidate, hashedPassword);
    }

    @Override
    public void createUser(User newUser) throws UserException {

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
            throw new CredentialsException("exceptions.usermanagement.no_credentials");
        }

        String enteredUserName = credentials.getUsername();
        String enteredPassword = credentials.getPassword();

        try {
            User possibleUser = readUser(enteredUserName);

            if (!checkUserCredentials(enteredUserName, enteredPassword)) {
                throw new UserException("exceptions.usermanagement.invalid_credentials");
            }
            if (!possibleUser.getActive()) {
                throw new UserException("exceptions.usermanagement.user_inactive");
            }

            HttpProfile userProfile = getProfileForUser(possibleUser);
            credentials.setUserProfile(userProfile);
        } catch (UserException e) {
            throw new CredentialsException(e.getMessage());
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
    public void updateUser(String userName, User newUserData) throws UserException {
        User userToChange = _userRepository.readUser(userName);

        if (userToChange == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        if (userToChange.getId() != newUserData.getId()) {
            throw new UserException("exceptions.usermanagement.different_ids");
        }
        // Check if there exists at least one user with role administrator
        if (userToChange.getRole().equals(SecurityRole.ROLE_ADMIN) && !newUserData.getRole().equals(SecurityRole.ROLE_ADMIN)) {
            List<User> userList = _userRepository.readUsers();
            boolean foundAdmin = false;
            for (User u : userList) {
                if (u.getRole().equals(SecurityRole.ROLE_ADMIN)) {
                    foundAdmin = true;
                    break;
                }
            }

            if (!foundAdmin) {
                throw new UserException("exceptions.usermanagement.at_least_one_admin");
            }
        }
        // Check if boss is valid
        if (_userRepository.readUser(newUserData.getUserNameBoss()) == null) {
            throw new UserException("exceptions.usermanagement.invalid_boss");
        }

        _userRepository.updateUser(newUserData);

    }

    @Override
    public void deleteUser(String userName) throws UserException {
        User userToDelete = _userRepository.readUser(userName);

        if (userToDelete == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        // Check if its not the last admin
        if (userToDelete.getRole().equals(SecurityRole.ROLE_ADMIN)) {
            List<User> userList = _userRepository.readUsers();
            boolean foundAdmin = false;
            for (User u : userList) {
                if (u.getRole().equals(SecurityRole.ROLE_ADMIN) && !u.getUserName().equals(userToDelete.getUserName())) {
                    foundAdmin = true;
                    break;
                }
            }

            if (!foundAdmin) {
                throw new UserException("exceptions.usermanagement.at_least_one_admin");
            }
        }
        _userRepository.deleteUser(userToDelete);
    }
}

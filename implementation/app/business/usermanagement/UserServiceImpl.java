package business.usermanagement;

import business.notification.NotificationException;
import business.notification.NotificationSender;
import business.notification.NotificationType;
import com.google.inject.Inject;
import infrastructure.UserRepository;
import models.Notification;
import models.User;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;

import java.util.List;

/**
 * Created by david on 03.04.16.
 */
class UserServiceImpl implements UserService, business.usermanagement.InternalUserManagement {


    private UserRepository _userRepository;
    private NotificationSender _notificationSender;


    @Inject
    public UserServiceImpl(UserRepository userRepository, NotificationSender notificationSender) {
        _userRepository = userRepository;
        _notificationSender = notificationSender;
    }


    @Override
    public User readUser(int id) throws UserException {
        return _userRepository.readUser(id);
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
    public List<User> readUsersOfBoss(int userId) throws UserException {
        return _userRepository.readUsersOfBoss(userId);
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
        if (userAlreadyExists(newUser.getUsername())) {
            throw new UserException("exceptions.usermanagement.user_already_exists");
        }

        try {
            _userRepository.readUser(newUser.getBoss().getId());
        } catch (Exception e) {
            throw new UserException("exceptions.usermanagement.invalid_boss");
        }

        _userRepository.createUser(newUser);

    }


    private boolean userAlreadyExists(String userName) {
        try {
            _userRepository.readUser(userName);
        } catch (UserException e) {
            return false;
        }

        return true;
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
        userProfile.addAttribute("username", possibleUser.getUsername());
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
        try {
            _userRepository.readUser(newUserData.getBoss().getId());
        } catch (Exception e) {
            throw new UserException("exceptions.usermanagement.invalid_boss");
        }

        _userRepository.updateUser(newUserData);

    }


    @Override
    public void deleteUser(String currentUserName, String userName) throws Exception {
        User userToDelete = _userRepository.readUser(userName);

        // Check if its not the last admin
        if (userToDelete.getRole().equals(SecurityRole.ROLE_ADMIN)) {
            List<User> userList = _userRepository.readUsers();
            boolean foundAdmin = false;
            for (User u : userList) {
                if (u.getRole().equals(SecurityRole.ROLE_ADMIN) && !u.getUsername().equals(userToDelete.getUsername())) {
                    foundAdmin = true;
                    break;
                }
            }

            if (!foundAdmin) {
                throw new UserException("exceptions.usermanagement.at_least_one_admin");
            }
        }

        notifyBossAboutDelete(currentUserName, userToDelete);

        _userRepository.deleteUser(userToDelete);
    }


    private void notifyBossAboutDelete(String currentUserName, User userToDelete) throws UserNotFoundException, NotificationException {
        User admin = _userRepository.readUser(currentUserName);
        Notification informBossNotification = new Notification(
                NotificationType.INFORMATION,
                "Fired " + userToDelete.getLastName() + " " + userToDelete.getFirstName(),
                admin,
                userToDelete.getBoss());
        _notificationSender.sendNotification(informBossNotification);
    }
}

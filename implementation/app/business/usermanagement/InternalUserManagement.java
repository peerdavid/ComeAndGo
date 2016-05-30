package business.usermanagement;

import models.User;

import java.util.List;

/**
 * Created by paz on 25.04.16.
 */
public interface InternalUserManagement {
    User readUser(int id) throws UserException;

    User readUser(String username) throws UserException;

    List<User> readUsers() throws Exception;

    List<User> readUsersOfBoss(int userId) throws Exception;

    void validateBossOfUserOrPersonnelManagerOrUserItself(int userId, int toTestBossId) throws Exception;
    void validateBossOfUserOrPersonnelManager(int userId, int toTestBossId) throws Exception;
}

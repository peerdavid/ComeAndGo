package business.usermanagement;

import business.UseCases;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface UserManagement extends UseCases {

    void createUser(User userToRegister) throws UserException;

    void updateUser(String userName, User newUserData) throws UserException;

    void deleteUser(String currentUserName, String userName) throws Exception;

    User readUser(String userName) throws UserException;

    List<User> readUsers() throws UserException;

    List<User> readBosses();

    List<User> readAdmins();

}

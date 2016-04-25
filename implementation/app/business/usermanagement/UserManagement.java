package business.usermanagement;

import business.UseCases;
import business.UserException;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface UserManagement extends UseCases {

    void createUser(User userToRegister) throws UserException;

    void updateUser(String userName, User newUserData) throws UserException;

    void deleteUser(String userName) throws UserException;

    User readUser(String userName) throws UserException;

    List<User> readUsers() throws UserException;

    boolean checkUserCredentials(String userName, String password) throws UserException;


}

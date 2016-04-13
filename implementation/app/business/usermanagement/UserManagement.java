package business.usermanagement;

import business.UseCases;
import business.UserException;
import model.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface UserManagement extends UseCases {

    void registerUser(User userToRegister) throws UserException;

    void changeUserData(String userName, User newUserData) throws UserException;

    void deleteUser(String userName) throws UserException;

    User getUserData(String userName) throws UserException;

    List<User> getAllUsers() throws UserException;

    boolean checkUserCredentials(String userName, String password) throws UserException;


}

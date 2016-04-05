package business.usermanagement;

import business.UseCases;
import model.User;

/**
 * Created by david on 29.03.16.
 */
public interface UserManagement extends UseCases {

    void registerUser(String userName, String password, String role, String firstName, String lastName, String email) throws Exception;

    void changeUserData(String userName, User newUserData) throws Exception;

    void deleteUser(String userName) throws Exception;

    User getUserData(String userName);

    boolean checkUserCredentials(String userName, String password);




}

package business.usermanagement;

import business.UseCases;

/**
 * Created by david on 29.03.16.
 */
public interface UserManagement extends UseCases {
    void registerUser(String userName, String password, String role, String firstName, String lastName, String email) throws Exception;
}

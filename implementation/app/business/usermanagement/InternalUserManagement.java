package business.usermanagement;

import javassist.NotFoundException;
import models.User;

/**
 * Created by paz on 25.04.16.
 */
public interface InternalUserManagement {
    User readUser(int id) throws UserNotFoundException;
}

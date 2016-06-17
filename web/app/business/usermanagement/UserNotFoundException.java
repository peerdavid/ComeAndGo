package business.usermanagement;

/**
 * Created by paz on 25.04.16.
 */
public class UserNotFoundException extends UserException {
    public UserNotFoundException() { super(); }
    public UserNotFoundException(String message) { super(message); }
    public UserNotFoundException(String message, Throwable cause) { super(message, cause); }
    public UserNotFoundException(Throwable cause) { super(cause); }
}

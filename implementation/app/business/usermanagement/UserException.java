package business.usermanagement;

/**
 * Created by paz on 07.04.16.
 */
public class UserException extends Exception {
    private Object[] _arguments;

    public UserException() { super(); }
    public UserException(String message) { super(message); }
    public UserException(String message, Object... arguments) {
        super(message);

        _arguments = arguments;
    }
    public UserException(String message, Throwable cause) { super(message, cause); }
    public UserException(Throwable cause) { super(cause); }

    public Object[] getArguments(){
        return _arguments;
    }
}
package tirol.comeandgo.business.api;

/**
 * Created by david on 24.05.16.
 */
public class ClientResult {

    private String mUseCase;
    private int mStatusCode;
    private String mMessage;

    public ClientResult(String useCase, int statusCode, String message){
        mUseCase = useCase;
        mStatusCode = statusCode;
        mMessage = message;
    }

    public String getUseCase(){
        return mUseCase;
    }

    public int getStatusCode(){
        return mStatusCode;
    }

    public String getMessage(){
        return mMessage;
    }
}

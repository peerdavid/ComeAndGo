package tirol.comeandgo.business.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by david on 24.05.16.
 */
public class Settings {

    private final static String PREF_NAME = "COME_AND_GO_PREFS";
    private final static String PREF_HOST = "HOST";
    private final static String PREF_PORT = "PORT";
    private final static String PREF_USERNAME = "USERNAME";
    private final static String PREF_PASSWORD = "PASSWORD";

    private SharedPreferences _prefs;

    public Settings(Context context){
        _prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setPort(int port){
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putInt(PREF_PORT, port);
        editor.commit();
    }

    public int getPort(){
        return _prefs.getInt(PREF_PORT, 9000);
    }

    public void setHost(String host){
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(PREF_HOST, host);
        editor.commit();
    }

    public String getHost(){
        return _prefs.getString(PREF_HOST, "192.168.10.116");
    }

    public void setUserName(String userName){
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(PREF_USERNAME, userName);
        editor.commit();
    }

    public String getUserName(){
        return _prefs.getString(PREF_USERNAME, "admin");
    }

    public void setPassword(String password){
        SharedPreferences.Editor editor = _prefs.edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }

    public String getPassword(){
        return _prefs.getString(PREF_PASSWORD, "");
    }

}

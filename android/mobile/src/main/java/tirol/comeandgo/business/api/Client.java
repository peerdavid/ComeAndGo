package tirol.comeandgo.business.api;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 23.05.16.
 */
public class Client {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String COME = "come";
    public static final String GO = "go";
    public static final String START_BREAK = "startBreak";
    public static final String END_BREAK = "endBreak";
    public static final String READ_STATE = "readState";

    private static final String COOKIES_HEADER = "Set-Cookie";

    private List<ClientResultListener> mListener;
    private java.net.CookieManager mCookieManager;
    private String mHost;
    private String mApiVersion;
    private String mUrl;
    private String mUserName;
    private String mPassword;

    /**
     *
     * @param host Ex.: 192.168.10.12:9000
     */
    public Client(String host, String apiVersion, String userName, String password){
        mHost = host;
        mCookieManager = new java.net.CookieManager();
        mApiVersion = apiVersion;
        mUrl = String.format("http://%s/api/%s/", mHost, mApiVersion);
        mListener = new ArrayList<>();
        mUserName = userName;
        mPassword = password;
    }


    public void setOnResultListener(ClientResultListener listener){
        mListener.add(listener);
    }


    public void removeOnResultListener(ClientResultListener listener){
        mListener.remove(listener);
    }


    public void come(){
        runUseCase(METHOD_POST, COME, true);
    }


    public void go(){
        runUseCase(METHOD_POST, GO, true);
    }


    public void startBreak(){
        runUseCase(METHOD_POST, START_BREAK, true);
    }


    public void endBreak(){
        runUseCase(METHOD_POST, END_BREAK, true);
    }


    public void readState(){
        runUseCase(METHOD_GET, READ_STATE, false);
    }


    private void runUseCase(final String method, final String useCase, final boolean updateState) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int response = login();
                    Log.d("tirol.comeandgo.app", "The login response is: " + response);

                    response = sendHttpRequest(method, useCase);
                    Log.d("tirol.comeandgo.app", "The " + useCase + " response is: " + response);

                    // Update real state
                    if(updateState) {
                        response = sendHttpRequest(METHOD_GET, READ_STATE);
                        Log.d("tirol.comeandgo.app", "The " + useCase + " response is: " + response);
                    }

                    response = logout();
                    Log.d("tirol.comeandgo.app", "The logout response is: " + response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private int login() throws IOException {
        URL url = new URL(String.format("http://%s/login?client_name=default", mHost));
        HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod(METHOD_POST);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);

        byte[] postData = ("username=" + mUserName + "&password=" + mPassword).getBytes( StandardCharsets.UTF_8 );
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setUseCaches(false);
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
        }

        int response = conn.getResponseCode();
        Log.d("tirol.comeandgo.app", "The login response is: " + response);

        conn.connect();
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        if(cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                Log.d("tirol.comeandgo.app", "Setting cookie " + cookie);
            }
        }
        return response;
    }


    private int logout(){
        try {
            URL url2 = new URL(String.format("http://%s/logout", mHost));
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            initConn(conn, METHOD_POST);
            conn.connect();
            return conn.getResponseCode();
        }catch (Exception e){
            return -1;
        }
    }


    private int sendHttpRequest(String method, String useCase){
        try {
            URL url2 = new URL(mUrl + useCase);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            initConn(conn, method);
            conn.connect();

            int statusCode = conn.getResponseCode();
            String resultBody = readBody(conn, statusCode);

            ClientResult result = new ClientResult(useCase, statusCode, resultBody);
            for(ClientResultListener listener : mListener){
                listener.onResult(result);
            }

            return statusCode;
        } catch (Exception e) {
            return -1;
        }
    }


    @Nullable
    private String readBody(HttpURLConnection conn, int statusCode) throws IOException {
        InputStreamReader in = 200 <= statusCode && statusCode <= 299
                ? new InputStreamReader((conn.getInputStream()))
                : new InputStreamReader((conn.getErrorStream()));

        BufferedReader br = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();
        String resultBody = br.readLine();
        while(resultBody != null){
            sb.append(resultBody);
            resultBody = br.readLine();
        }

        in.close();
        br.close();
        return sb.toString();
    }


    public void initConn(HttpURLConnection conn, String method) throws Exception{
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(method);

        if (mCookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie",
                    TextUtils.join(";", mCookieManager.getCookieStore().getCookies()));
        }
    }
}

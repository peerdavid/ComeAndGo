package tirol.comeandgo.business;

import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 23.05.16.
 */
public class CommunicationWrapper {
    static final String COOKIES_HEADER = "Set-Cookie";

    private java.net.CookieManager mCookieManager;
    private String mServerUrl;

    /**
     *
     * @param url Ex.: 192.168.10.12:9000
     */
    public CommunicationWrapper(String url){
        mServerUrl = url;
        mCookieManager = new java.net.CookieManager();
    }


    public void come(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int response = login("admin", "admin");
                    Log.d("tirol.comeandgo.app", "The login response is: " + response);

                    response = sendHttpRequest("GET", "come");
                    Log.d("tirol.comeandgo.app", "The come response is: " + response);

                    response = sendHttpRequest("POST", "logout");
                    Log.d("tirol.comeandgo.app", "The logout response is: " + response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void go(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int response = login("admin", "admin");
                    Log.d("tirol.comeandgo.app", "The login response is: " + response);

                    response = sendHttpRequest("GET", "go");
                    Log.d("tirol.comeandgo.app", "The go response is: " + response);

                    response = sendHttpRequest("POST", "logout");
                    Log.d("tirol.comeandgo.app", "The logout response is: " + response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private int login(String username, String password) throws IOException {
        URL url = new URL("http://" + mServerUrl + "/login?client_name=default");
        HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);

        byte[] postData = ("username=" + username + "&password=" + password).getBytes( StandardCharsets.UTF_8 );
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


    private int sendHttpRequest(String method, String url){
        try {
            URL url2 = new URL("http://" + mServerUrl + "/" + url);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            initConn(conn, method);
            conn.connect();
            return conn.getResponseCode();
        }catch (Exception e){
            return -1;
        }
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

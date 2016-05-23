package tirol.comeandgo.business;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
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


    public void come(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //
                    // LOGIN
                    //
                    URL url = new URL("http://192.168.10.116:9000/login?client_name=default");
                    Log.d("tirol.comeandgo.app", "Try login on " + url);
                    HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(false);

                    byte[] postData = "username=admin&password=admin".getBytes( StandardCharsets.UTF_8 );
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
                    conn.setUseCaches(false);
                    try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                        wr.write( postData );
                    }
                    //conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("tirol.comeandgo.app", "The login response is: " + response);


                    //
                    // Set cookie
                    //
                    conn.connect();
                    java.net.CookieManager msCookieManager = new java.net.CookieManager();
                    Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                    if(cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                            Log.d("tirol.comeandgo.app", "Setting cookie " + cookie);
                        }
                    }

                    //
                    // COME
                    //
                    URL url2 = new URL("http://192.168.10.116:9000/come");
                    HttpURLConnection conn2  = (HttpURLConnection) url2.openConnection();
                    conn2.setReadTimeout(10000);
                    conn2.setConnectTimeout(15000);
                    conn2.setRequestMethod("GET");
                    if(msCookieManager.getCookieStore().getCookies().size() > 0) {
                        //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                        conn2.setRequestProperty("Cookie",
                                TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    }
                    conn2.connect();
                    response = conn2.getResponseCode();
                    Log.d("tirol.comeandgo.app", "The come response is: " + response);


                    //
                    // ToDo: LOGOUT
                    //


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

package slyx.communication;

import slyx.jsonsimple.*;
import slyx.jsonsimple.parser.*;
import slyx.utils.Gender;
import slyx.utils.Jison;
import slyx.utils.Me;
import slyx.utils.User;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class API_auth {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String port = "3000";
    private final String route = "http://127.0.0.1:"+ this.port +"/api/auth";

    public API_auth() {
    }

    public static User connect(String email, String password) {
        User u = new User(1, "Antoine", "Janvier", 21, "antoine@janvier.com", "tototiti");
        u.setConnected(true);
        return u;
    }

    public void sendDisconnectionRequest() throws Exception {
        URL url = new URL(this.route + "/sign_out");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        rd.close();
    }

    public User sendConnectionRequest(String email, String password) throws Exception {
        /*
        URL
         */
        String url = this.route + "/sign_in";
        URL obj = new URL(url);
        /*
        Connection
         */
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        /*
        Set request parameters
         */
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters = "email=" + email + "&pwd=" + password;
        con.setDoOutput(true);
        /*
        Data output
         */
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        /*
        Data input
         */
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        /*
        Read
         */
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONParser jsonParser = new JSONParser();

        Object o = jsonParser.parse(response.toString());
        JSONObject j = (JSONObject) o;

//        System.out.println(response.toString());
//        System.out.println(j);
//        System.out.println(j.get("firstname"));

        Me me = new Me(
                Math.toIntExact((long) j.get("id")),
                j.get("firstname").toString(),
                j.get("lastname").toString(),
                Math.toIntExact((long) j.get("age")),
                j.get("email").toString()
                );
        me.setConnected(true);
        System.out.println(me.toString());
        return me;
    }
}

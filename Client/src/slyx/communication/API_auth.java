package slyx.communication;

import slyx.utils.Gender;
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
    public static User connect(String email, String password) {
        User u = new User("Antoine", "Janvier", 21, "antoine@janvier.com", MALE, "tototiti");
        u.setConnected(true);
        return u;
    }
    // HTTP POST request


    public static void sendConnectionRequest(String email, String password) throws Exception {
        final String USER_AGENT = "Mozilla/5.0";
        String url = "http://127.0.0.1:3000/api/auth/sign_in";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "email=" + email + "&pwd=" + password;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }
}

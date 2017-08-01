package slyx.communication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import slyx.utils.Jison;
import slyx.utils.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static slyx.utils.Gender.FEMALE;
import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class API_contact {

    private final String USER_AGENT = "Mozilla/5.0";
    private final String port = "3000";
    private final String route = "http://127.0.0.1:"+ this.port +"/api";

    public static User[] getContacts(int idUser) {
        User u1 = new User(1, "Antoine", "Janvier", 21, "antoine@janvier.com", MALE, "tototiti");
        u1.setConnected(true);
        User u2 = new User(2, "Tata", "Titi", 19, "tata@toto.com", FEMALE, "tototiti");
        u2.setConnected(false);

        User[] users = new User[2];
        users[0] = u1;
        users[1] = u2;

        return users;
    }

//    public User[] getContacts(int idUser) throws IOException {
//        StringBuilder result = new StringBuilder();
//        URL url = new URL(this.route + "/contacts");
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String line;
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//        rd.close();
//        Jison j = new Jison(result.toString());
//        int nb_contacts = j.getNbOf("User");
//        User[] users = new User[nb_contacts];
//        for (int i = 0; i < nb_contacts; i++) {
//
//        }
//
//    }
}

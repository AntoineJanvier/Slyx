package slyx.communication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import slyx.utils.User;

import static slyx.utils.Gender.FEMALE;
import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class API_contact {
    public static User[] getContacts(int idUser) {
        User u1 = new User("Antoine", "Janvier", 21, "antoine@janvier.com", MALE, "tototiti");
        u1.setConnected(true);
        User u2 = new User("Tata", "Titi", 19, "tata@toto.com", FEMALE, "tototiti");
        u2.setConnected(false);

        User[] users = new User[2];
        users[0] = u1;
        users[1] = u2;

        return users;
    }
}

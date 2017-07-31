package slyx.communication;

import slyx.utils.Gender;
import slyx.utils.User;

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
}

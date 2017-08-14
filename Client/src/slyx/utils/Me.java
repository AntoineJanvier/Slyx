package slyx.utils;

import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Me extends User {

    private static Me instance = null;

    public static synchronized Me getInstance() {
        if (instance == null) {
            return null;
        }
        return instance;
    }

    public void setNULL() {
        instance = null;
    }

    public Me(int id, String firstname, String lastname, int age, String email, String picture) {
        super(id, firstname, lastname, age, email, picture);
    }
}

package slyx.utils;

import static slyx.utils.Gender.MALE;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Me extends User {

    private static Me instance = null;

    public static Me getInstance() {
        return instance;
    }

    public void setNULL() {
        instance = null;
    }

    public Me(int id, String firstname, String lastname, int age, String email) {
        super(id, firstname, lastname, age, email, MALE);
    }
}

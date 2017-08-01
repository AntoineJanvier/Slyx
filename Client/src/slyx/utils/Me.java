package slyx.utils;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Me extends User {

    private static Me me;

    public static Me getInstance() {
//        if (me == null)
//            me = new Me();
        return me;
    }


    public Me(String firstname, String lastname, int age, String email, Gender gender) {
        super(firstname, lastname, age, email, gender);
    }
}

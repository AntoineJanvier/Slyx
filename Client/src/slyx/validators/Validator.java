package slyx.validators;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class Validator {
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidPassword(String password) {
        int min = 6;
        int max = 32;
        if (password.length() < min || password.length() > max)
            return false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!Character.isAlphabetic(c))
                return false;
        }
        return true;
    }
}

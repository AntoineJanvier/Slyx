package slyx.exceptions;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SlyxErrors {
    public static String getError(SlyxError e) {
        switch (e) {
            case ERR_CONNECTION:
                return "Unable to connect to server...";
            case ERR_EMAIL:
                return "Incorrect email...";
            case ERR_PASSWORD:
                return "Incorrect password...";
            case ERR_SOCKET:
                return "Problem with server connection...";
            default:
                return "Unknown error...";
        }
    }
}

package slyx.exceptions;

import java.net.ConnectException;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SocketConnectionException extends ConnectException {
    public SocketConnectionException() {
        System.out.println("Socket connection refused");
    }
}

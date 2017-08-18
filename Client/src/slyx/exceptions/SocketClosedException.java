package slyx.exceptions;

import java.net.SocketException;

/**
 * Created by Antoine Janvier
 * on 18/08/17.
 */
public class SocketClosedException extends SocketException {
    public SocketClosedException() {
        System.out.println("EXCEPTION : Socket closed");
    }
}

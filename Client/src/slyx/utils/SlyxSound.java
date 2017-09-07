package slyx.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import slyx.communication.SlyxSocket;

import java.io.File;
import java.io.IOException;

import static slyx.utils.SlyxAnnotation.Type.SOUND;

/**
 * Created by Antoine Janvier
 * on 19/08/17.
 */

@SlyxAnnotation(todo = "Add new sounds", type = SOUND)
public class SlyxSound {
    private static MediaPlayer login = new MediaPlayer(new Media(new File("src/slyx/utils/sounds/login1.mp3").toURI().toString()));
    private static MediaPlayer notification = new MediaPlayer(new Media(new File("src/slyx/utils/sounds/pop.mp3").toURI().toString()));

    public static void playSound(String s) throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        if (!slyxSocket.getMe().isSetting_sounds()) return;
        double volume = (double) slyxSocket.getMe().getSetting_volume() / 100;
        if (s != null) {
            switch (s) {
                case "LOGIN":
                    if (!slyxSocket.getMe().isSetting_connections()) return;
                    login.stop();
                    login.setVolume(volume);
                    login.play();
                    break;
                case "NOTIFICATION":
                    if (!slyxSocket.getMe().isSetting_messages()) return;
                    notification.stop();
                    notification.setVolume(volume);
                    notification.play();
                    break;
                default:
                    System.out.println("Wrong type of SlyxSound called");
            }
        }
    }
}

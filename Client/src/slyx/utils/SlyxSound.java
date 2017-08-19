package slyx.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Created by Antoine Janvier
 * on 19/08/17.
 */
public class SlyxSound {
    private static MediaPlayer login = new MediaPlayer(new Media(new File("src/slyx/utils/sounds/login1.mp3").toURI().toString()));
    private static MediaPlayer notification = new MediaPlayer(new Media(new File("src/slyx/utils/sounds/pop.mp3").toURI().toString()));

    public static void playSound(String s) {
        if (s != null) {
            switch (s) {
                case "LOGIN":
                    login.stop();
                    login.play();
                    break;
                case "NOTIFICATION":
                    notification.stop();
                    notification.play();
                    break;
                default:
                    System.out.println("Wrong type of SlyxSound called");
            }
        }
    }
}

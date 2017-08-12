package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

/**
 * Created by Antoine Janvier
 * on 31/07/17.
 */
public class SettingsController {

    // Top buttons
    @FXML
    Button button_save_settings;
    @FXML
    Button button_cancel_settings;

    // SOUND
    @FXML
    CheckBox checkBox_sounds;
    @FXML
    Slider slider_volume_sound;

    // NOTIFICATIONS
    @FXML
    CheckBox checkBox_notifications;
    @FXML
    CheckBox checkBox_notifications_calls;
    @FXML
    CheckBox checkBox_notifications_messages;
    @FXML
    CheckBox checkBox_notifications_connections;

    public void saveSettings() {
        /*
        TODO : Find a way to save settings in function of the User, DB, file or other
         */
    }

    public void closePanelSettings() {
        Stage stage = (Stage) button_cancel_settings.getScene().getWindow();
        stage.close();
    }
}

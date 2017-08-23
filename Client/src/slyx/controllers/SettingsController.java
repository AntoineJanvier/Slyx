package slyx.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import slyx.communication.SlyxSocket;
import slyx.utils.User;

import java.io.IOException;

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

    private void setCheckedSettings(CheckBox a, CheckBox b, CheckBox c) {
        boolean v = a.isSelected();
        if (!v) {
            checkBox_notifications.setSelected(false);
        } else if (b.isSelected() && c.isSelected()) {
            checkBox_notifications.setSelected(true);
        }
    }

    public void initialize() throws IOException {

        checkBox_sounds.setOnMouseClicked(event -> {
            boolean v = checkBox_sounds.isSelected();
            if (v) {
                slider_volume_sound.setDisable(false);
            } else {
                slider_volume_sound.setDisable(true);
            }
        });

        checkBox_notifications.setOnMouseClicked(event -> {
            boolean v = checkBox_notifications.isSelected();
            checkBox_notifications_calls.setSelected(v);
            checkBox_notifications_messages.setSelected(v);
            checkBox_notifications_connections.setSelected(v);
        });
        checkBox_notifications_calls.setOnMouseClicked(event -> setCheckedSettings(
                checkBox_notifications_calls,
                checkBox_notifications_messages,
                checkBox_notifications_connections
        ));
        checkBox_notifications_messages.setOnMouseClicked(event -> setCheckedSettings(
                checkBox_notifications_messages,
                checkBox_notifications_calls,
                checkBox_notifications_connections
        ));
        checkBox_notifications_connections.setOnMouseClicked(event -> setCheckedSettings(
                checkBox_notifications_connections,
                checkBox_notifications_messages,
                checkBox_notifications_calls
        ));


        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        User me = slyxSocket.getMe();

        // Sounds
        if (me.isSetting_sounds()) {
            checkBox_sounds.setSelected(true);
        }
        // Volume
        if (checkBox_sounds.isSelected()) {
            slider_volume_sound.setDisable(false);
            slider_volume_sound.setValue(
                    me.getSetting_volume() >= 0 && me.getSetting_volume() <= 100
                            ? me.getSetting_volume()
                            : (double) 50
            );
        } else {
            slider_volume_sound.setDisable(true);
        }

        // Notifications
        if (me.isSetting_notifications()) {
            checkBox_notifications.setSelected(true);

            checkBox_notifications_calls.setSelected(true);
            checkBox_notifications_messages.setSelected(true);
            checkBox_notifications_connections.setSelected(true);
        }
        // Notifications Calls
        if (me.isSetting_calls()) {
            checkBox_notifications_calls.setSelected(true);
        }
        // Notifications Messages
        if (me.isSetting_messages()) {
            checkBox_notifications_messages.setSelected(true);
        }
        // Notifications Connections
        if (me.isSetting_connections()) {
            checkBox_notifications_connections.setSelected(true);
        }
    }

    public void saveSettings() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        slyxSocket.sendUpdateMySettings(
                checkBox_sounds.isSelected(),
                (int) Math.round(slider_volume_sound.getValue()),
                checkBox_notifications.isSelected(),
                checkBox_notifications_calls.isSelected(),
                checkBox_notifications_messages.isSelected(),
                checkBox_notifications_connections.isSelected()
        );
    }

    public void closePanelSettings() {
        Stage stage = (Stage) button_cancel_settings.getScene().getWindow();
        stage.close();
    }
}

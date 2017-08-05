package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import slyx.Version;
import slyx.communication.SlyxSocket;
import slyx.utils.Me;
import slyx.validators.Validator;

import java.io.IOException;

import static slyx.exceptions.SlyxError.ERR_CONNECTION;
import static slyx.exceptions.SlyxError.ERR_EMAIL;
import static slyx.exceptions.SlyxError.ERR_PASSWORD;
import static slyx.exceptions.SlyxErrors.getError;

public class LoginController {
    @FXML
    AnchorPane anchorPane_general;
    @FXML
    Label label_connection;
    @FXML
    Label label_get_update;
    @FXML
    TextField tf_email;
    @FXML
    PasswordField tf_password;
    @FXML
    Button btn_sign_in;
    @FXML
    Hyperlink hyperlink_sign_up;
    @FXML
    Hyperlink hyperlink_get_update;
    @FXML
    Label label_error_hint;

    @FXML
    public void launch_next_screen() throws Exception {
        SlyxSocket socket =  SlyxSocket.getInstance();

        // Get connection information
        String u_email = tf_email.getText();
        String u_pwd = tf_password.getText();

        // Test inputs
        if (!Validator.isValidEmailAddress(u_email))
            label_error_hint.setText(getError(ERR_EMAIL));
        else if (!Validator.isValidPassword(u_pwd))
            label_error_hint.setText(getError(ERR_PASSWORD));
        else {

            // If all seems ok, request the server a connection
            socket.sendConnectionRequest(u_email, u_pwd);

            // Test singleton of Me object to know if we can launch the app just if he is connected
            Me me = Me.getInstance();
            if (me.isConnected()) {

                // Close login window
                Stage stage = (Stage) btn_sign_in.getScene().getWindow();
                stage.close();

                // Launch app window
                Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/slyx.fxml"));
                Stage next_stage = new Stage();
                next_stage.setTitle("Slyx");
                next_stage.setScene(new Scene(next_root));
                next_stage.show();
            } else {
                label_error_hint.setText(getError(ERR_CONNECTION));
            }
        }
    }
    public void initialize() {
        try {
            SlyxSocket slyxSocket = SlyxSocket.getInstance();
            String version = slyxSocket.sendGetUpdateRequest();

            String[] v = version.split("\\.");
            if (Integer.parseInt(v[0]) > Version.MAJOR) {
                label_get_update.setText("A new major update is available, download it");
                hyperlink_get_update.setText("here");
            } else if (Integer.parseInt(v[1]) > Version.INTERMEDIATE) {
                label_get_update.setText("A new intermediate update is available, download it");
                hyperlink_get_update.setText("here");
            } else if (Integer.parseInt(v[2]) > Version.MINOR) {
                label_get_update.setText("A new minor update is available, download it");
                hyperlink_get_update.setText("here");
            } else {
                label_get_update.setText("Up to date");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import slyx.communication.API_auth;
import slyx.utils.User;
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
    TextField tf_email;
    @FXML
    PasswordField tf_password;
    @FXML
    Button btn_sign_in;
    @FXML
    Hyperlink hyperlink_sign_up;
    @FXML
    Label label_error_hint;

    @FXML
    public void launch_next_screen() throws IOException {
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/slyx.fxml"));

        String u_email = tf_email.getText();
        String u_pwd = tf_password.getText();

        if (!Validator.isValidEmailAddress(u_email))
            label_error_hint.setText(getError(ERR_EMAIL));
        else if (!Validator.isValidPassword(u_pwd))
            label_error_hint.setText(getError(ERR_PASSWORD));
        else {
            User u = API_auth.connect(u_email, u_pwd);

            if (u.isConnected()) {
                Stage stage = (Stage) btn_sign_in.getScene().getWindow();
                stage.close();

                Stage next_stage = new Stage();
                next_stage.setTitle("Slyx");
                next_stage.setScene(new Scene(next_root));
                next_stage.show();
            } else {
                label_error_hint.setText(getError(ERR_CONNECTION));
            }
        }
    }
}

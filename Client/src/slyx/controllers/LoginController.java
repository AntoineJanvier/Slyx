package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    AnchorPane anchorPane_general;
    @FXML
    Label label_connection;
    @FXML
    TextField tf_email;
    @FXML
    TextField tf_password;
    @FXML
    Button btn_sign_in;
    @FXML
    Hyperlink hyperlink_sign_up;

    @FXML
    public void launch_next_screen() throws IOException {
        Parent next_screen = FXMLLoader.load(getClass().getResource("scenes/slyx.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Slyx");
        next_stage.setScene(new Scene(next_screen));
        next_stage.show();
    }
}

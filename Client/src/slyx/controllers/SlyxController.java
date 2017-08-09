package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import slyx.communication.API_auth;
import slyx.communication.API_contact;
import slyx.communication.SlyxSocket;
import slyx.utils.Me;
import slyx.utils.User;

import java.io.IOException;

/**
 * Created by Antoine Janvier
 * on 30/07/17.
 */
public class SlyxController {
    @FXML
    BorderPane borderPane_general;
    @FXML
    HBox hBox_top;
    @FXML
    VBox vBox_left;
    @FXML
    Button btn_send_message;
    @FXML
    TextField tf_message_to_send;
    @FXML
    Button btn_disconnection;
    @FXML
    WebView webView_my_icon;
    @FXML
    Label label_my_id;
    @FXML
    Label label_my_firstname;
    @FXML
    Label label_my_lastname;
    @FXML
    Label label_my_email;

    public void disconnect() throws IOException {
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
        Stage stage = (Stage) btn_disconnection.getScene().getWindow();
        stage.close();
        Stage next_stage = new Stage();
        next_stage.setTitle("Slyx");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void initialize() throws IOException {
        // Set default icon for my profile
        WebEngine engine = webView_my_icon.getEngine();
        String url = "http://localhost:3000/images/my_icon_profile.png";
        engine.load(url);

        // Set my informations
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        User me = SlyxSocket.getMe();
        label_my_id.setText(me.getId() + "");
        label_my_firstname.setText(me.getFirstname());
        label_my_lastname.setText(me.getLastname());
        label_my_email.setText(me.getEmail());

        User[] contacts = slyxSocket.sendGetContactsRequest(SlyxSocket.getMe());
        for (User u : contacts) {
            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contact.fxml"));
            Label l_firstname = (Label) p.lookup("#label_firstname");
            Label l_lastname = (Label) p.lookup("#label_lastname");
            l_firstname.setText(u.getFirstname());
            l_lastname.setText(u.getLastname());
            vBox_left.getChildren().add(p);
        }
    }
}

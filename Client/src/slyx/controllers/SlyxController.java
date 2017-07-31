package slyx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import slyx.communication.API_contact;
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
    TextArea ta_message_to_send;

    public void initialize() throws IOException {
        User[] contacts = API_contact.getContacts(1);
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

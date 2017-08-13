package slyx.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slyx.communication.SlyxSocket;
import slyx.utils.User;

import java.io.IOException;

/**
 * Created by Antoine Janvier
 * on 12/08/17.
 */
public class AddContactController {
    @FXML
    ScrollPane scrollPane_add_contact;
    @FXML
    VBox vBox_in_scrollPane;
    @FXML
    Button button_done_add_contact;

    public void closeWindow() {
        Stage stage = (Stage) button_done_add_contact.getScene().getWindow();
        stage.close();
    }

    public void initialize() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        User[] contacts = slyxSocket.sendGetContactsRequest(SlyxSocket.getMe());
        for (User u : contacts) {
            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contact.fxml"));
            Label l_firstname = (Label) p.lookup("#label_firstname");
            Label l_lastname = (Label) p.lookup("#label_lastname");
            Label l_connected = (Label) p.lookup("#label_connected");
            l_firstname.setText(u.getFirstname());
            l_lastname.setText(u.getLastname());
            l_connected.setVisible(false);

            Button b = new Button("Add " + u.getFirstname() + " " + u.getLastname());
            b.setOnAction(event -> {
                slyxSocket.sendAddContactRequest(u.getId());
                p.setDisable(true);
                b.setDisable(true);
            });

            vBox_in_scrollPane.getChildren().add(p);
            vBox_in_scrollPane.getChildren().add(b);
        }
    }
}

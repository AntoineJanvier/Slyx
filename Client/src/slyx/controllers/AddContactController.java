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

        slyxSocket.sendGetUsersNotInContactList(slyxSocket.getMe());
        User[] contacts = slyxSocket.getOtherUsers();
        for (User u : contacts) {
            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactRequest.fxml"));
            Label l_name = (Label) p.lookup("#label_name");
            l_name.setText(u.getFirstname() + " " + u.getLastname());

            Button button_Remove = (Button) p.lookup("#button_reject_request");
            button_Remove.setVisible(false);

            Button button_Add = (Button) p.lookup("#button_add_accept_request");
            button_Add.setOnAction(event -> {
                slyxSocket.sendAddContactRequest(u.getId());
                p.setDisable(true);
                button_Add.setDisable(true);
            });

            vBox_in_scrollPane.getChildren().add(p);
        }
    }
}

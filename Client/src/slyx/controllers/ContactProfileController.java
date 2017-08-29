package slyx.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Created by Antoine Janvier
 * on 13/08/17.
 */
public class ContactProfileController {
    @FXML
    Label label_firstname;
    @FXML
    Label label_lastname;
    @FXML
    Label label_email;
    @FXML
    Button button_remove_contact;
    @FXML
    Button button_call_contact;

    public void initialize() {
    }
}

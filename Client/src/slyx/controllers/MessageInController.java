package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import slyx.utils.Message;

/**
 * Created by Antoine Janvier
 * on 19/08/17.
 */
public class MessageInController {
    @FXML
    AnchorPane anchorPane_message;
    @FXML
    Label label_content;
    @FXML
    Label label_date;

    public void setMessage(Message message) {
        label_content.setText(message.getContent());
        label_date.setText(message.getSent().toString());
    }

    public void initialize() {
        // CSS
        anchorPane_message
                .getStylesheets().add(getClass().getResource("/slyx/css/messageIn.css").toExternalForm());
    }
}

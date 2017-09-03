package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import slyx.utils.Message;
import slyx.validators.MessageContentValidator;

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
    @FXML
    ImageView imageView_from;

    public void setMessage(Message message, Image image, String toTest) {
        if (MessageContentValidator.isURL(toTest)) {
            label_content.setText("");
            if (MessageContentValidator.isWebContent(toTest)) {
                MessageContentValidator.setWebContent(toTest, 340, 340, 80, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
            } else if (MessageContentValidator.isImageContent(toTest)) {
                MessageContentValidator.setImage(toTest, 340, 340, 80, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
            } else {
                label_content.setText(message.getContent() + " : Unsupported URL embedding");
            }
        } else {
            label_content.setText(message.getContent());
        }
        label_date.setText(message.getSent().toString());
        imageView_from.setImage(image);
    }

    public void initialize() {
        // CSS
        anchorPane_message
                .getStylesheets().add(getClass().getResource("/slyx/css/messageIn.css").toExternalForm());
    }
}

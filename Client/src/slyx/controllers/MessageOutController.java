package slyx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import slyx.utils.Message;
import slyx.validators.ImageValidator;

/**
 * Created by Antoine Janvier
 * on 10/08/17.
 */
public class MessageOutController {
    @FXML
    AnchorPane anchorPane_message;
    @FXML
    Label label_content;
    @FXML
    Label label_date;
    @FXML
    ImageView imageView_from;

    public void setMessage(Message message, Image image, String toTest) {
        if (ImageValidator.isImage(toTest)) {
            label_content.setText("");
            if (ImageValidator.isGIF(toTest)) {
                ImageValidator.setGIF(toTest, 340, 340, 270, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
            } else {
                ImageValidator.setImage(toTest, 340, 340, 270, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
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
                .getStylesheets().add(getClass().getResource("/slyx/css/messageOut.css").toExternalForm());
    }
}

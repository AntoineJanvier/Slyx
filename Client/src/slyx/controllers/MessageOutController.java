package slyx.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import slyx.utils.Message;
import slyx.validators.MessageContentValidator;

import javax.management.Notification;
import java.io.IOException;

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
    @FXML
    Button button_deleteMessage;
    @FXML
    Hyperlink hyperlink_url;

    public void setMessage(Message message, Image image, String toTest) {
        if (MessageContentValidator.isURL(toTest)) {
            label_content.setText("");
            if (MessageContentValidator.isWebContent(toTest)) {
                MessageContentValidator.setWebContent(toTest, 340, 340, 270, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
            } else if (MessageContentValidator.isImageContent(toTest)) {
                MessageContentValidator.setImage(toTest, 340, 340, 270, 10, 360, 370, label_content,
                        label_date, anchorPane_message);
            } else {
                hyperlink_url.setVisible(true);
                hyperlink_url.setLayoutX(100);
                hyperlink_url.setText(message.getContent());
                hyperlink_url.setOnMouseClicked(event -> {
                    try {
                        new ProcessBuilder("x-www-browser", message.getContent()).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                label_content.setVisible(false);
            }
        } else {
            label_content.setText(message.getContent());
        }
        label_date.setText(message.getSent().toString());
        imageView_from.setImage(image);

        button_deleteMessage.setOnMouseClicked(event -> {
            // TODO : Delete our own messages
        });
    }

    public void initialize() {
        // CSS
        anchorPane_message
                .getStylesheets().add(getClass().getResource("/slyx/css/messageOut.css").toExternalForm());

        //ImageView imageView = new ImageView(new Image("/slyx/images/red-cross.png"));
        //imageView.setFitHeight(20);
        //imageView.setFitWidth(20);
        //button_deleteMessage.setGraphic(imageView);
        button_deleteMessage.setVisible(false);
        hyperlink_url.setVisible(false);
        anchorPane_message.notify();
    }
}

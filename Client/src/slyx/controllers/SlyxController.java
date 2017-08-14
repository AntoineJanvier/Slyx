package slyx.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slyx.communication.SlyxSocket;
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
    AnchorPane anchorPane_right;
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
    ImageView imageView_my_icon;
    @FXML
    Label label_my_firstname;
    @FXML
    Label label_my_lastname;
    @FXML
    Label label_my_email;
    @FXML
    VBox vBox_request;

//    public void getMessagesOfContactSelected() throws IOException {
//        SlyxSocket slyxSocket = SlyxSocket.getInstance();
//        Message[] messages = slyxSocket.sendGetMessagesOfContactRequest(SlyxSocket.getMe(), new User());
//        for (Message message : messages) {
//            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contact.fxml"));
//            Label l_firstname = (Label) p.lookup("#label_content");
//            Label l_lastname = (Label) p.lookup("#label_lastname");
//            l_content.setText(message.getContent());
//            l_sent.setText(message.getSent());
//            vBox_left.getChildren().add(p);
//        }
//    }

    public void launchAddNewContactWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/addContact.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Add Contact");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void launchSettingsWindow() throws IOException {
        // Launch Settings window
        Parent next_root = FXMLLoader.load(getClass().getResource("/slyx/scenes/settings.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Settings");
        next_stage.setScene(new Scene(next_root));
        next_stage.show();
    }

    public void disconnect() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        slyxSocket.close();

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
        String imagePath = "http://localhost:3000/images/my_icon_profile.png";
        Image image = new Image(imagePath);
        imageView_my_icon.setImage(image);

        // Set my informations
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        User me = SlyxSocket.getMe();
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

            p.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactProfile.fxml"));
                        Label l_firstname = (Label) p.lookup("#label_firstname");
                        Label l_lastname = (Label) p.lookup("#label_lastname");
                        Label l_email = (Label) p.lookup("#label_email");
                        ImageView imageView = (ImageView) p.lookup("#imageView_contact_icon");
                        l_firstname.setText(u.getFirstname());
                        l_lastname.setText(u.getLastname());
                        l_email.setText(u.getEmail());
                        imageView.setImage(new Image(u.getPicture()));
                        anchorPane_right.getChildren().clear();
                        anchorPane_right.getChildren().add(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            vBox_left.getChildren().add(p);
        }

        User[] requests = slyxSocket.sendGetPendingContactRequests(SlyxSocket.getMe());
        for (User u : requests) {
            Parent p = FXMLLoader.load(getClass().getResource("/slyx/scenes/contactRequest.fxml"));
            Label l_name = (Label) p.lookup("#label_name");
            l_name.setText(u.getFirstname() + " " + u.getLastname());
            Button button_Reject = (Button) p.lookup("#button_reject_request");
            button_Reject.setOnAction(event -> {
                slyxSocket.sendRejectContactRequest(u.getId());
                p.setDisable(true);
                button_Reject.setDisable(true);
            });
            Button button_Accept = (Button) p.lookup("#button_add_accept_request");
            button_Accept.setOnAction(event -> {
                slyxSocket.sendAcceptContactRequest(u.getId());
                p.setDisable(true);
                button_Accept.setDisable(true);
            });
            vBox_request.getChildren().add(p);
        }
    }
}

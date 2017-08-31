package slyx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import slyx.communication.SlyxSocket;
import slyx.utils.SlyxSound;
import slyx.utils.User;

import java.io.IOException;

/**
 * Created by Antoine Janvier
 * on 30/07/17.
 */
public class SlyxController {
    // Containers
    @FXML
    AnchorPane anchorPane_top;
    @FXML
    AnchorPane anchorPane_right;
    @FXML
    BorderPane borderPane_general;
    @FXML
    HBox hBox_top;
    @FXML
    ScrollPane scrollPane_messages;
    @FXML
    Tab tabPaneTab_requests;
    @FXML
    TabPane tabPane_left;
    @FXML
    VBox vBox_left;
    @FXML
    VBox vBox_messages;
    @FXML
    VBox vBox_request;

    // Buttons
    @FXML
    Button btn_send_message;
    @FXML
    Button btn_disconnection;
    @FXML
    Button button_addNewContact;

    // Utils
    @FXML
    ImageView imageView_my_icon;
    @FXML
    TextField tf_message_to_send;

    // Labels
    @FXML
    Label label_my_firstname;
    @FXML
    Label label_my_lastname;
    @FXML
    Label label_my_email;

    /**
     * Launch the window where user can add a new contact
     * @throws IOException : When FXMLLoader.load(...) fail
     */
    private void launchAddNewContactWindow() throws IOException {
        // Launch Settings window
        Parent parent = FXMLLoader.load(getClass().getResource("/slyx/scenes/addContact.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Add Contact");
        next_stage.setScene(new Scene(parent));
        next_stage.show();
    }

    /**
     * Launch the window where user can change its settings (notifications, volume sound, sounds, ...)
     * @throws IOException : When FXMLLoader.load(...) fail
     */
    public void launchSettingsWindow() throws IOException {
        // Launch Settings window
        Parent parent = FXMLLoader.load(getClass().getResource("/slyx/scenes/settings.fxml"));
        Stage next_stage = new Stage();
        next_stage.setTitle("Settings");
        next_stage.setScene(new Scene(parent));
        next_stage.show();
    }

    /**
     * Close the link between client and server
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void disconnect() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();
        slyxSocket.sendDisconnectionEvent();
        slyxSocket.setMe(null);

        Parent parent = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
        Stage stage = (Stage) btn_disconnection.getScene().getWindow();
        stage.close();
        Stage next_stage = new Stage();
        next_stage.setTitle("Slyx");
        next_stage.setScene(new Scene(parent));
        next_stage.show();
    }

    /**
     * Initialize all components of the Slyx app : Contacts, user requests, events etc...
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void initialize() throws IOException {
        SlyxSound.playSound("LOGIN");

        // Add CSS
        borderPane_general.getStylesheets().add(getClass().getResource("/slyx/css/slyx.css").toExternalForm());

        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        button_addNewContact.setOnMouseClicked(event -> {
            try {
                launchAddNewContactWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Set my informations
        User me = slyxSocket.getMe();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/myProfile.fxml"));
        Parent p = fxmlLoader.load();
        MyProfileController myProfileController = fxmlLoader.getController();
        myProfileController.setMe(me);
        anchorPane_top.getChildren().add(p);

        tf_message_to_send.setDisable(true);
        btn_send_message.setDisable(true);

        refresh(slyxSocket);
    }

    /**
     * Call the refresh functions in the Slyx app window
     * @param slyxSocket : Instance of SlyxSocket (singleton)
     * @throws IOException : When refreshContacts() or refreshContactRequests() calls
     */
    private void refresh(SlyxSocket slyxSocket) throws IOException {
        slyxSocket.sendGetContactsRequest();
        slyxSocket.sendGetPendingContactRequests();
        slyxSocket.sendGetUsersNotInContactList(slyxSocket.getMe());

        refreshContacts();
        refreshContactRequests();
    }

    /**
     * Load the view of a contact and add it to contact list with specific user informations
     * @param user : The user to print in contact list
     * @throws IOException : On FXMLLoader.load(...) call
     */
    private void refreshContactsInContactList(User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/contact.fxml"));
        Parent parent = fxmlLoader.load();
        ContactController contactController = fxmlLoader.getController();
        contactController.setContact(user, vBox_messages, tf_message_to_send, btn_send_message, anchorPane_right, scrollPane_messages);

        vBox_left.getChildren().add(parent);
    }

    /**
     * Refresh the contact list
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    public void refreshContacts() throws IOException {
        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        // Get current contacts
        User[] contacts = slyxSocket.getContacts();

        // Clear the VBox
        slyxSocket.clearVBox(vBox_left);

        // Set all contacts in the contact area
        for (User u : contacts)
            refreshContactsInContactList(u);

        // If needed, clear VBox to reset contact list (in case of connection or other things)
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    try {
                        // If refresh is needed
                        if (slyxSocket.needToRefreshContacts) {
                            // Clear VBox
                            slyxSocket.clearVBox(vBox_left);
                            // Get contacts
                            User[] users = slyxSocket.getContacts();
                            // Set contacts in contact list
                            for (User u : users)
                                refreshContactsInContactList(u);
                            slyxSocket.needToRefreshContacts = false;
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    if (slyxSocket.needToClearCurrent) {
                        slyxSocket.needToClearCurrent = false;
                        slyxSocket.clearVBox(vBox_messages);
                        anchorPane_right.getChildren().clear();
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Refresh the user requests in the Requests Tab
     * @throws IOException : When getting the instance of the SlyxSocket (singleton)
     */
    private void refreshContactRequests() throws IOException {
        // Set the contact request in PENDING state in the request area
        SlyxSocket slyxSocket = SlyxSocket.getInstance();

        // Clear vBox_request
        slyxSocket.clearVBox(vBox_request);

        // Add refresh when new request income
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    try {
                        if (slyxSocket.hasNewPendingRequest) {
                            slyxSocket.clearVBox(vBox_request);
                            for (User u : slyxSocket.getUserRequests()) {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/slyx/scenes/contactRequest.fxml"));
                                Parent p = fxmlLoader.load();
                                ContactRequestController contactRequestController = fxmlLoader.getController();
                                contactRequestController.setWithUser(u, false);
                                vBox_request.getChildren().add(p);
                            }
                            slyxSocket.hasNewPendingRequest = false;
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}

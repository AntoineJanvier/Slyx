package slyx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import slyx.utils.SlyxAnnotation;
import slyx.utils.SlyxAnnotationProcessor;

import java.io.IOException;

import static slyx.utils.SlyxAnnotation.Type.EXCEPTION;

@SlyxAnnotation(todo = "Redirect all errors to a log file with the ErrorExit class", type = EXCEPTION)
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/slyx/scenes/login.fxml"));
            primaryStage.setTitle("Slyx");
            root.getStylesheets().add(getClass().getResource("/slyx/css/login.css").toExternalForm());
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> ((Stage) primaryStage.getScene().getWindow()).close());
        } catch (IOException e) {
            System.out.println("Error while launching the app");
        }
    }


    public static void main(String[] args) {
        SlyxAnnotationProcessor slyxAnnotationProcessor = new SlyxAnnotationProcessor();
        launch(args);
    }
}

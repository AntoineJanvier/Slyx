package slyx.validators;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

public class ImageValidator {
    public static boolean isImage(String imageUrl) {
        String[] strings = imageUrl.split("\\.");
        for (String string : strings) {
            if ("png".equals(string)
                    || "PNG".equals(string)
                    || "jpg".equals(string)
                    || "JPG".equals(string)
                    || "gif".equals(string)
                    || "GIF".equals(string))
                return true;
        }
        return false;
    }
    public static boolean isGIF(String imageUrl) {
        String[] strings = imageUrl.split("\\.");
        for (String string : strings) {
            if ("gif".equals(string) || "GIF".equals(string))
                return true;
        }
        return false;
    }
    public static void setImage(String s, double w, double h, double x, double y, double lC, double lD,
                                Label lContent, Label lDate, AnchorPane m) {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(s));
        imageView.setFitWidth(w);
        imageView.setFitHeight(h);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        lContent.setMinHeight(lC);
        lDate.setLayoutY(lD);
        m.getChildren().add(imageView);
    }
    public static void setGIF(String s, double w, double h, double x, double y, double lC, double lD,
                                Label lContent, Label lDate, AnchorPane m) {
        WebView webView = new WebView();
        webView.getEngine().load(s);
        webView.setMaxWidth(w);
        webView.setMaxHeight(h);
        webView.setLayoutX(x);
        webView.setLayoutY(y);
        lContent.setMinHeight(lC);
        lDate.setLayoutY(lD);
        m.getChildren().add(webView);
    }
}

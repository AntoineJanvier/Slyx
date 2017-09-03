package slyx.validators;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageContentValidator {
    public static boolean isWebContent(String imageUrl) {
        if (!isURL(imageUrl)) return false;
        String[] strings = imageUrl.split("\\.");
        for (String string : strings) {
            if (testOnWebTypes(string)) return true;
            else {
                String[] strings1 = string.split("/");
                for (String string1 : strings1) {
                    if (testOnWebTypes(string1)) return true;
                }
            }
        }
        return false;
    }
    public static boolean isImageContent(String imageUrl) {
        if (!isURL(imageUrl)) return false;
        String[] strings = imageUrl.split("\\.");
        for (String string : strings) {
            if (testOnTypes(string)) return true;
            else {
                String[] strings1 = string.split("/");
                for (String string1 : strings1) {
                    if (testOnTypes(string1)) return true;
                }
            }
        }
        return false;
    }

    public static boolean isURL(String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        return m.matches();
    }
    public static boolean testOnTypes(String test) {
        return ("png".equals(test)
                || "PNG".equals(test)
                || "jpg".equals(test)
                || "JPG".equals(test)
                || "gif".equals(test)
                || "GIF".equals(test)
                || "youtube".equals(test)
                || "youtu".equals(test));
    }
    private static boolean testOnWebTypes(String test) {
        return ("gif".equals(test)
                || "GIF".equals(test)
                || "youtube".equals(test)
                || "youtu".equals(test));
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
    public static void setWebContent(String s, double w, double h, double x, double y, double lC, double lD,
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

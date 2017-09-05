package slyx.utils;

/**
 * Created by Antoine Janvier
 * on 05/09/17.
 */
public class MessageContentEncrypt {
    public static String encrypt(String content) {
        if (content == null) return null;
        if (content.length() == 0) return content;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '"') {
                stringBuilder.append('\"');
            } else {
                stringBuilder.append(content.charAt(i));
            }
//            if ((content.charAt(i) >= 'a' && content.charAt(i) < 'z')
//                    || (content.charAt(i) >= 'A' && content.charAt(i) < 'Z')) {
//                stringBuilder.append((char) (content.charAt(i) + 1));
//            } else {
//                if (content.charAt(i) == '"') {
//                    stringBuilder.append('\"');
//                } else if (content.charAt(i) == '[') {
//                    stringBuilder.append("\\");
//                    stringBuilder.append('[');
//                } else if (content.charAt(i) == ']') {
//                    stringBuilder.append("\\");
//                    stringBuilder.append(']');
//                } else {
//                    stringBuilder.append(content.charAt(i));
//                }
//            }
        }
        return stringBuilder.toString();
    }
    public static String decrypt(String content) {
        if (content == null) return null;
        if (content.length() == 0) return content;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            if ((content.charAt(i) > 'a' && content.charAt(i) <= 'z')
                    || (content.charAt(i) > 'A' && content.charAt(i) <= 'Z')) {
                stringBuilder.append((char) (content.charAt(i) - 1));
            } else {
                stringBuilder.append(content.charAt(i));
            }
        }
        return stringBuilder.toString();
    }
}

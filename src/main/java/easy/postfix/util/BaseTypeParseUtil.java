package easy.postfix.util;

public class BaseTypeParseUtil {

    private BaseTypeParseUtil() {
    }


    public static boolean parseBoolean(String value, boolean defaultVal) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ignored) {
            return defaultVal;
        }
    }

    public static Integer parseInt(String value, Integer defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return defaultVal;
        }
    }

}

package easy.util;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 语言处理工具
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/05 11:33
 **/

public class LanguageUtil {

    private static final Pattern SPLIT_CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
    private static final Pattern IS_CAMEL_CASE_PATTERN = Pattern.compile("^[a-z]+([A-Z][a-z0-9]*)*$");
    private static final Pattern IS_SNAKE_CASE_PATTERN = Pattern.compile("^[a-z]+(_[a-z0-9]+)*$");
    private static final Pattern IS_ALL_ENGLISH_PATTERN = Pattern.compile("^[A-Za-z ]+$");


    private LanguageUtil() {

    }

    /**
     * 是否中文字符
     *
     * @param c
     * @return boolean
     * @author mabin
     * @date 2023/9/5 11:35
     **/
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 是否含有中文字符
     *
     * @param str
     * @return boolean
     * @author mabin
     * @date 2023/9/5 11:35
     **/
    public static boolean isContainsChinese(String str) {
        if (Objects.isNull(str) || str.isBlank()) {
            return false;
        }
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否中文字符串
     *
     * @param str
     * @return boolean
     * @author mabin
     * @date 2023/9/5 11:35
     **/
    public static boolean isAllChinese(String str) {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (!isChinese(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否全是英文字母和空格
     *
     * @param str
     * @return boolean
     * @author mabin
     * @date 2023/11/13 9:25
     */
    public static boolean isAllEnglish(String str) {
        return IS_ALL_ENGLISH_PATTERN.matcher(str).matches();
    }

    /**
     * 是否驼峰命名
     *
     * @param str
     * @return boolean
     * @author mabin
     * @date 2023/11/13 9:30
     */
    public static boolean isCamelCase(String str) {
        return IS_CAMEL_CASE_PATTERN.matcher(str).matches();
    }

    /**
     * 是否为蛇形命名
     *
     * @param str
     * @return boolean
     * @author mabin
     * @date 2023/11/13 9:52
     */
    public static boolean isSnakeCase(String str) {
        return IS_SNAKE_CASE_PATTERN.matcher(str).matches();
    }

    /**
     * 分割驼峰命名字符串
     *
     * @param str
     * @return java.lang.String[]
     * @author mabin
     * @date 2023/11/13 9:37
     */
    public static String[] splitCamelCase(String str) {
        return SPLIT_CAMEL_CASE_PATTERN.matcher(str).replaceAll(" ").split(" ");
    }

}

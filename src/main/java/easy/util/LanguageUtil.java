package easy.util;

/**
 * 语言处理工具
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/05 11:33
 **/

public class LanguageUtil {

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

}

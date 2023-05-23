package easy.base;

import easy.form.Statistics;

/**
 * EasyChar公共基础属性信息
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 14:42
 **/

public class Constants {

    public static final int TOTAL_LENGTH = 45;

    public static final int SPLIT_LENGTH = 2;

    public static final char PREFIX_CHAR = '/';

    public static final String EASY_CHAR_KEY = "easy_char_key";

    public static final String DEFAULT_STRING = "， , 。 . ： : ； ; ！ ! ？ ? “ \" ” \" ‘ ' ’ ' 【 [ 】 ] （ ( ） ) 「 { 」 } 《 < 》 >"
            .replace(" ", "\n");

    public static Statistics STATISTICS = null;

    public static final String TOTAL_CONVERT_COUNT = "total_convert_count";


}

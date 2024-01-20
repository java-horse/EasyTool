package easy.base;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * EasyChar公共基础属性信息
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 14:42
 **/
public class Constants {

    public static final String PLUGIN_NAME = "EasyTool";
    public static final int TOTAL_LENGTH = 45;
    public static final int SPLIT_LENGTH = 2;
    public static final char PREFIX_CHAR = '/';
    public static final String AT = "@";
    public static final String DEFAULT_STRING = "， , 。 . ： : ； ; ！ ! ？ ? “ \" ” \" ‘ ' ’ ' 【 [ 】 ] （ ( ） ) 「 { 」 } 《 < 》 >".replace(" ", "\n");
    public static final String GITEE_URL = "https://gitee.com/milubin/easy-tool-plugin";
    public static final String JETBRAINS_URL = "https://plugins.jetbrains.com/plugin/21589-easytool/reviews";
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the");
    public static final String UID = "serialVersionUID";
    public static final String PROMPT_TEMPLATE = "我希望你充当一个英语翻译助手。我会用任何语言与你交谈，你将自动分析并检测语言，并将语言翻译成 en(英文)，或者 zh(中文) 目标语言，只输出翻译结果且不需要标点符号。待翻译文本是：%s，目标语言是：%s";
    public static final String BREAK_LINE = "&br;";

    /**
     * 插件持久化变量
     */
    public static class Persistence {
        /**
         * 通用
         */
        public interface COMMON {
            String LAST_NOTIFY_TIME = PLUGIN_NAME + "." + COMMON.class.getSimpleName() + ".last_notify_time";
            String TRANSLATE_CONFIG_LAST_NOTIFY_TIME = PLUGIN_NAME + "." + COMMON.class.getSimpleName() + ".translate_config_last_notify_time";
        }

        /**
         * 中英文字符转换
         */
        public interface CONVERT {
            String EASY_CHAR_KEY = PLUGIN_NAME + "." + CONVERT.class.getSimpleName() + ".easy_char_key";
        }

        /**
         * Mybatis Log
         */
        public interface MYBATIS_LOG {
            String PREPARING_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".preparing";
            String PARAMETERS_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".parameters";
            String KEYWORDS_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".keywords";
            String INSERT_SQL_COLOR_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".insert_sql_color";
            String DELETE_SQL_COLOR_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".delete_sql_color";
            String UPDATE_SQL_COLOR_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".update_sql_color";
            String SELECT_SQL_COLOR_KEY = PLUGIN_NAME + "." + MYBATIS_LOG.class.getSimpleName() + ".select_sql_color";
        }

    }

    /**
     * 通用数字
     */
    public interface NUM {
        int ZERO = 0;
        int ONE = 1;
        int TWO = 2;
        int THREE = 3;
        int FOUR = 4;
        int FIVE = 5;
        int SIX = 6;
        int SEVEN = 7;
        int EIGHT = 8;
        int NINE = 9;
        int TEN = 10;
        int EIGHTY = 80;
        int HUNDRED = 100;
    }

}

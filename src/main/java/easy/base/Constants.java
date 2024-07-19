package easy.base;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * EasyChar公共基础属性信息
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 14:42
 **/
public class Constants {

    public static final String PLUGIN_ID = "easy.char";
    public static final String PLUGIN_NAME = "EasyTool";
    public static final int TOTAL_LENGTH = 45;
    public static final int SPLIT_LENGTH = 2;
    public static final char PREFIX_CHAR = '/';
    public static final String AT = "@";
    public static final String DEFAULT_STRING = "， , 。 . ： : ； ; ！ ! ？ ? “ \" ” \" ‘ ' ’ ' 【 [ 】 ] （ ( ） ) 「 { 」 } 《 < 》 >".replace(" ", "\n");
    public static final String GITEE_URL = "https://gitee.com/milubin/easy-tool-plugin";
    public static final String JETBRAINS_URL = "https://plugins.jetbrains.com/plugin/21589-easytool/reviews";
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the", "of");
    public static final String BREAK_LINE = "&br;";
    public static final Set<String> BASE_TYPE_SET = Sets.newHashSet("byte", "short", "int", "long", "char", "float", "double", "boolean");
    public static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

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
            String AUTO_UPDATE_LAST_NOTIFY_TIME = PLUGIN_NAME + "." + COMMON.class.getSimpleName() + ".auto_update_last_notify_time";
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

        /**
         * Git Commit Message
         */
        public interface GIT_COMMIT_MESSAGE {
            String LAST_COMMIT_MESSAGE = PLUGIN_NAME + "." + GIT_COMMIT_MESSAGE.class.getSimpleName() + ".last_commit_message";
        }

        /**
         * Background Image Plus 功能扩展
         */
        public interface BACKGROUND_IMAGE {
            String FOLDER = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_folder";
            String IMAGE_COUNT = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_count";
            String CHANGE_SWITCH = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_change_switch";
            String INTERVAL = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_interval";
            String TIME_UNIT = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_time_unit";
            String CHANGE_SCOPE = PLUGIN_NAME + "." + BACKGROUND_IMAGE.class.getSimpleName() + "background_images_change_scope";
        }

        /**
         * 开源大模型相关
         */
        public interface OPEN_MODEL {
            String WEN_XIN_ACCESS_TOKEN = PLUGIN_NAME + "." + OPEN_MODEL.class.getSimpleName() + "access_token";
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
        int TWENTY = 20;
        int EIGHTY = 80;
        int HUNDRED = 100;
        int TWO_HUNDRED = 200;
        int THREE_HUNDRED = 300;
        int ONE_THOUSAND = 1000;
        int TWO_THOUSAND = 2000;
    }

    /**
     * JavaDoc相关
     */
    public interface JAVA_DOC {
        String DEFAULT_AUTHOR = StringUtils.isBlank(System.getProperty("user.name")) ? "admin" : System.getProperty("user.name");
        String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm";
    }

    /**
     * 注解属性相关
     */
    public interface ANNOTATION_ATTR {
        String VALUE = "value";
        String NOTES = "notes";
        String REQUIRED = "required";
        String METHOD = "method";
        String TAGS = "tags";
        String DESCRIPTION = "description";
        String MAX = "max";
        String MIN = "min";
        String SUMMARY = "summary";
        String NAME = "name";
        String TITLE = "title";
        String PATTERN = "pattern";
        String HIDDEN = "hidden";
        String IN = "in";
        String CRON = "cron";
        String PATH = "path";
        String DEPRECATED = "deprecated";
        String NULLABLE = "nullable";
        String HTTP_METHOD = "httpMethod";
        String PARAM_TYPE = "paramType";
        String DATA_TYPE = "dataType";
        String DATA_TYPE_CLASS = "dataTypeClass";
        String ALLOW_MULTIPLE = "allowMultiple";
        String REQUIRED_MODE = "requiredMode";
        String DEFAULT_VALUE = "defaultValue";
    }

    /**
     * JavaDoc文档标签
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.Constants
     * @date 2024/05/13 11:03
     */
    public interface DOC_TAG {
        String CATEGORY = "menu";
        String MODULE = "module";
        String DESCRIPTION = "description";
        String DESCRIPTION_UP = "Description";
        String DEPRECATED = "deprecated";
        String PARAM = "param";
        String MOCK = "mock";
        String IGNORE = "ignore";
        String SEE = "see";
        String LINK = "link";
        String TAGS = "tags";
    }

}

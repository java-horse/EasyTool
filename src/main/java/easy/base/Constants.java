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
     * 持久化数据变量名
     */
    public interface STATE_VAR {
        String LAST_NOTIFY_TIME = "last_notify_time";
        String EASY_CHAR_KEY = "easy_char_key";
        String TOTAL_CONVERT_COUNT = "total_convert_count";
        String DAY_CONVERT_COUNT = "day_convert_count";
    }

    /**
     * Spring注解
     */
    public interface SPRING_ANNOTATION {
        String REQUEST_PARAM_TEXT = "org.springframework.web.bind.annotation.RequestParam";
        String REQUEST_HEADER_TEXT = "org.springframework.web.bind.annotation.RequestHeader";
        String PATH_VARIABLE_TEXT = "org.springframework.web.bind.annotation.PathVariable";
        String REQUEST_BODY_TEXT = "org.springframework.web.bind.annotation.RequestBody";
        String CONTROLLER_ANNOTATION = "org.springframework.stereotype.Controller";
        String REST_CONTROLLER_ANNOTATION = "org.springframework.web.bind.annotation.RestController";
        String FEIGN_CLIENT_ANNOTATION = "org.springframework.cloud.openfeign.FeignClient";
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


    /**
     * postfix快捷方式名称
     */
    public interface POSTFIX_SHORTCUT_NAME {
        String GENERATE_GETTER = "Generate Getter";
        String GENERATE_SETTER = "Generate Setter";
        String GENERATE_SETTER_NO_DEFAULT_VAL = "Generate Setter with no default val";
        String GENERATE_CONVERT = "Generate Convert";
    }

}

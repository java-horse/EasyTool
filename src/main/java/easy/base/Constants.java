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
    public static final String EASY_CHAR_KEY = "easy_char_key";
    public static final String DEFAULT_STRING = "， , 。 . ： : ； ; ！ ! ？ ? “ \" ” \" ‘ ' ’ ' 【 [ 】 ] （ ( ） ) 「 { 」 } 《 < 》 >".replace(" ", "\n");
    public static final String TOTAL_CONVERT_COUNT = "total_convert_count";
    public static final String DAY_CONVERT_COUNT = "day_convert_count";
    public static final String GITEE_URL = "https://gitee.com/milubin/easy-tool-plugin";
    public static final String JETBRAINS_URL = "https://plugins.jetbrains.com/plugin/21589-easytool/reviews";
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the");
    public static final String UID = "serialVersionUID";
    public static final String PROMPT_TEMPLATE = "我希望你充当一个英语翻译助手。我会用任何语言与你交谈，你将自动分析并检测语言，并将语言翻译成 en(英文)，或者 zh(中文) 目标语言，只输出翻译结果且不需要标点符号。待翻译文本是：%s，目标语言是：%s";

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

}

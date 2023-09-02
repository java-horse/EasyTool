package easy.base;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

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
    public static final String TOTAL_CONVERT_COUNT = "total_convert_count";
    public static final String GITEE_URL = "https://gitee.com/milubin/easy-char";
    public static final String JETBRAINS_URL = "https://plugins.jetbrains.com/plugin/21589-easychar/reviews";

    /**
     * 可用翻译平台集合
     */
    public static final Set<String> ENABLE_TRANSLATOR_SET = ImmutableSet.of(TRANSLATE.BAIDU, TRANSLATE.ALIYUN,
            TRANSLATE.YOUDAO, TRANSLATE.TENCENT);

    /**
     * 翻译平台
     */
    public interface TRANSLATE {
        String BAIDU = "百度翻译";
        String ALIYUN = "阿里翻译";
        String YOUDAO = "有道翻译";
        String TENCENT = "腾讯翻译";
    }


}

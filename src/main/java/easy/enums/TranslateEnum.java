package easy.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 翻译渠道枚举处理
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/04 16:05
 **/

public enum TranslateEnum {

    BAIDU("百度翻译", "http://api.fanyi.baidu.com/api/trans/vip/translate"),
    ALIYUN("阿里翻译", "http://mt.cn-hangzhou.aliyuncs.com/api/translate/web/general"),
    YOUDAO("有道翻译", "https://openapi.youdao.com/api"),
    TENCENT("腾讯翻译", "https://tmt.tencentcloudapi.com"),
    VOLCANO("火山翻译", "https://translate.volcengineapi.com");

    private final String translate;
    private final String url;


    /**
     * 获取全部翻译渠道名称
     *
     * @param
     * @return java.util.Set<java.lang.String>
     * @author mabin
     * @date 2023/9/4 16:09
     **/
    public static Set<String> getTranslator() {
        return Arrays.stream(values()).map(TranslateEnum::getTranslate).collect(Collectors.toSet());
    }


    TranslateEnum(String translate, String url) {
        this.translate = translate;
        this.url = url;
    }

    public String getTranslate() {
        return translate;
    }

    public String getUrl() {
        return url;
    }

}

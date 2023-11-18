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
    VOLCANO("火山翻译", "https://translate.volcengineapi.com"),
    XFYUN("讯飞翻译", "https://itrans.xf-yun.com/v1/its"),
    GOOGLE("谷歌翻译(API)", "https://translation.googleapis.com/language/translate/v2?q=%s&source=%s&target=%s&key=%s&format=text"),
    MICROSOFT("微软翻译", "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=%s&to=%s"),
    NIU("小牛翻译", "http://api.niutrans.com/NiuTransServer/translation?from=%s&to=%s&apikey=%s&src_text=%s"),
    CAIYUN("彩云翻译", "http://api.interpreter.caiyunai.com/v1/translator"),
    HUAWEI("华为翻译", "https://nlp-ext.cn-north-4.myhuaweicloud.com/v1/%s/machine-translation/text-translation"),
    GOOGLE_FREE("谷歌翻译(Free)", "http://translate.google.com/translate_a/single?client=gtx&dt=t&dj=1&ie=UTF-8&sl=%s&tl=%s&q=%s");

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

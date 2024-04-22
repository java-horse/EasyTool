package easy.enums;

/**
 * 翻译语言枚举
 *
 * @author mabin
 * @project EasyTool
 * @package easy.enums
 * @date 2024/03/18 15:08
 */
public enum TranslateLanguageEnum {

    AUTO("auto", "自动检测语言"),
    EN("en", "英文文本"),
    EN_US("en_US", "英文文本"),
    ZH("zh", "简体中文"),
    CN("cn", "简体中文"),
    ZH_CN("zh-CN", "简体中文"),
    ZH_HANS("zh-Hans", "简体中文"),
    ZH_CHS("zh-CHS", "简体中文"),
    ZH_TW("zh-TW", "繁体中文");

    public final String lang;
    public final String desc;

    TranslateLanguageEnum(String lang, String desc) {
        this.lang = lang;
        this.desc = desc;
    }

}

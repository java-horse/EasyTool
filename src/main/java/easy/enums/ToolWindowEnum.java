package easy.enums;

/**
 * 工具菜单栏Action名称枚举处理
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/10/14 13:52:55
 */
public enum ToolWindowEnum {

    PLUGIN_STATISTICS("使用统计", "EasyChar插件使用统计"),
    WECHAT_OFFICIAL("微信公众号", "EasyChar微信公众号"),
    PLUGIN_SETTING("插件设置", "EasyChar插件设置");

    ToolWindowEnum(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public final String title;
    public final String desc;

}

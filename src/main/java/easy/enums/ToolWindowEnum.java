package easy.enums;

import com.intellij.icons.AllIcons;
import easy.icons.EasyIcons;

import javax.swing.*;

/**
 * 工具菜单栏Action名称枚举处理
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/10/14 13:52:55
 */
public enum ToolWindowEnum {
    WECHAT_OFFICIAL("微信公众号", "微信公众号", EasyIcons.ICON.WECHAT_OFFICIAL),
    PLUGIN_SETTING("插件设置", "插件设置", EasyIcons.ICON.SETTING),
    SEARCH_API("搜索API", "搜索API", EasyIcons.ICON.API),
    FOR_UPDATE("检查更新", "检查插件最新版本", AllIcons.Ide.Notification.PluginUpdate),
    WIDGET("效率组件", "Widget效率小组件", EasyIcons.ICON.PUZZLE),
    TRANSLATE_BACKUP("翻译备份", "翻译备份", AllIcons.Debugger.Db_db_object);

    ToolWindowEnum(String title, String desc, Icon icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public final String title;
    public final String desc;
    public final Icon icon;

}

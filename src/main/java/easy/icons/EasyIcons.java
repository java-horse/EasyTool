package easy.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * 自定义Icon图标引入
 *
 * @project: EasyChar
 * @package: easy.icon
 * @author: mabin
 * @date: 2023/11/06 14:48:47
 */
public class EasyIcons {

    public interface ICON {
        Icon COMMON = IconLoader.getIcon("icons/common.svg", EasyIcons.class);
        Icon SWAGGER = IconLoader.getIcon("icons/swagger.svg", EasyIcons.class);
        Icon TRANSLATE = IconLoader.getIcon("icons/translate.svg", EasyIcons.class);
        Icon TEXT = IconLoader.getIcon("icons/text.svg", EasyIcons.class);
        Icon WEB = IconLoader.getIcon("icons/web.svg", EasyIcons.class);
        Icon LOGO = IconLoader.getIcon("icons/pluginIcon.svg", EasyIcons.class);
        Icon TOOL_LOGO = IconLoader.getIcon("icons/pluginToolIcon.svg", EasyIcons.class);
        Icon EDGE = IconLoader.getIcon("icons/edge.svg", EasyIcons.class);
        Icon CHROME = IconLoader.getIcon("icons/chrome.svg", EasyIcons.class);
        Icon OVERFLOW = IconLoader.getIcon("icons/stack-overflow.svg", EasyIcons.class);
        Icon BAIDU = IconLoader.getIcon("icons/baidu.svg", EasyIcons.class);
        Icon SO = IconLoader.getIcon("icons/360.svg", EasyIcons.class);
        Icon SOGOU = IconLoader.getIcon("icons/sogou.svg", EasyIcons.class);
        Icon DUCK_DUCK_GO = IconLoader.getIcon("icons/duckduckgo.svg", EasyIcons.class);
        Icon WECHAT_OFFICIAL = IconLoader.getIcon("icons/wechat-official.svg", EasyIcons.class);
        Icon SETTING = IconLoader.getIcon("icons/setting.svg", EasyIcons.class);
        Icon STATISTICS = IconLoader.getIcon("icons/statistics.svg", EasyIcons.class);
        Icon COPY = IconLoader.getIcon("icons/copy.svg", EasyIcons.class);
        Icon YANDEX = IconLoader.getIcon("icons/yandex.svg", EasyIcons.class);
    }

}

package easy.enums;

import easy.icons.EasyIcons;

import javax.swing.*;

/**
 * WEB在线搜索枚举
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/10/10 13:36:25
 */
public enum WebSearchEnum {

    BAIDU("Baidu", "http://www.baidu.com/s?wd=%s", "百度搜索", EasyIcons.ICON.BAIDU),
    BING("Bing", "http://www.bing.com/search?q=%s", "必应搜索", EasyIcons.ICON.EDGE),
    GOOGLE("Google", "http://www.google.com/search?q=%s", "谷歌搜索", EasyIcons.ICON.CHROME),
    STACK_OVERFLOW("StackOverflow", "https://www.google.com/search?q=site%3Astackoverflow.com%20", "Stack OVERFLOW 搜索", EasyIcons.ICON.OVERFLOW),
    SO("360", "https://www.so.com/s?q=%s", "360搜索", EasyIcons.ICON.SO),
    SO_GOU("Sogou", "https://www.sogou.com/web?q=%s", "搜狗搜索", EasyIcons.ICON.SOGOU),
    DUCK_DUCK_GO("DuckDuckGo", "https://duckduckgo.com/?q=%s", "DuckDuckGo搜索", EasyIcons.ICON.DUCK_DUCK_GO),
    YANDEX("Yandex", "https://yandex.com/search/?text=%s", "Yandex搜索", EasyIcons.ICON.YANDEX);

    public final String title;
    public final String templateUrl;
    public final String remark;
    public final Icon icon;

    WebSearchEnum(String title, String templateUrl, String remark, Icon icon) {
        this.title = title;
        this.templateUrl = templateUrl;
        this.remark = remark;
        this.icon = icon;
    }

}

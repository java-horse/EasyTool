package easy.enums;

/**
 * WEB在线搜索枚举
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/10/10 13:36:25
 */
public enum WebSearchEnum {

    BAIDU("Baidu", "http://www.baidu.com/s?wd=%s", "百度搜索"),
    BING("Bing", "http://www.bing.com/search?q=%s", "必应搜索"),
    GOOGLE("Google", "http://www.google.com/search?q=%s", "谷歌搜索"),
    STACK_OVERFLOW("StackOverflow", "https://www.google.com/search?q=site%3Astackoverflow.com%20", "Stack OVERFLOW 搜索"),
    SO("360", "https://www.so.com/s?q=%s", "360搜索"),
    SO_GOU("Sogou", "https://www.sogou.com/web?q=%s", "搜狗搜索"),
    DUCK_DUCK_GO("DuckDuckGo", "https://duckduckgo.com/?q=%s", "Duck搜索");

    public final String title;
    public final String templateUrl;

    public final String remark;

    WebSearchEnum(String title, String templateUrl, String remark) {
        this.title = title;
        this.templateUrl = templateUrl;
        this.remark = remark;
    }

}

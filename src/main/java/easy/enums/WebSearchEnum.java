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

    BAIDU("Baidu Search", "http://www.baidu.com/s?wd=%s","百度搜索"),
    BING("Bing Search", "http://www.bing.com/search?q=%s","必应搜索"),
    GOOGLE("Google Search", "http://www.google.com/search?q=%s","谷歌搜索"),
    STACK_OVERFLOW("Stack Overflow Search", "https://www.google.com/search?q=site%3Astackoverflow.com%20","Stack OVERFLOW 搜索"),
    SO("360 Search", "https://www.so.com/s?q=%s","360搜索"),
    SO_GOU("Sogou Search", "https://www.sogou.com/web?q=%s","搜狗搜索");

    public final String title;
    public final String templateUrl;

    public final String remark;

    WebSearchEnum(String title, String templateUrl, String remark) {
        this.title = title;
        this.templateUrl = templateUrl;
        this.remark = remark;
    }

}
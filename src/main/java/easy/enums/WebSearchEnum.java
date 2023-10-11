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

    BAIDU("Baidu Search", "http://www.baidu.com/s?wd=%s"),
    BING("Bing Search", "http://www.bing.com/search?q=%s"),
    GOOGLE("Google Search", "http://www.google.com/search?q=%s"),
    STACK_OVERFLOW("Stack Overflow Search", "https://www.google.com/search?q=site%3Astackoverflow.com%20%s");

    public final String title;
    public final String templateUrl;

    WebSearchEnum(String title, String templateUrl) {
        this.title = title;
        this.templateUrl = templateUrl;
    }

}

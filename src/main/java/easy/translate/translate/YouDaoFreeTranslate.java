package easy.translate.translate;

import cn.hutool.http.*;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 有道翻译免费翻译服务
 *
 * @author mabin
 * @project EasyTool
 * @package easy.translate.translate
 * @date 2024/03/14 10:10
 */

public class YouDaoFreeTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(YouDaoFreeTranslate.class);
    private final Pattern TRANSLATE_PATTERN = Pattern.compile("<ul id=\"translateResult\">(.*?)</ul>", Pattern.DOTALL);

    /**
     * 中译英
     *
     * @param chStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:36
     **/
    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.AUTO.lang, "ZH_CN2EN");
    }

    /**
     * 英译中
     *
     * @param enStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:36
     **/
    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.AUTO.lang, "EN2ZH_CN");
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return
     */
    private String translate(String text, String source, String target) {
        Map<String, Object> paramsMap = new HashMap<>(8);
        paramsMap.put("inputtext", text);
        paramsMap.put("type", target);
        try (HttpResponse httpResponse = HttpRequest.post(TranslateEnum.YOUDAO_FREE.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue())
                .form(paramsMap).execute()) {
            String body = httpResponse.body();
            if (StringUtils.isBlank(body)) {
                return StringUtils.EMPTY;
            }
            Matcher matcher = TRANSLATE_PATTERN.matcher(body);
            if (matcher.find()) {
                String group = StringUtils.trim(matcher.group(Constants.NUM.ONE)).replace(StringUtils.LF, StringUtils.EMPTY);
                return StringUtils.trim(HtmlUtil.unwrapHtmlTag(group, "li")).replace(StringUtils.LF, StringUtils.EMPTY);
            }
        } catch (Exception e) {
            log.error(TranslateEnum.YOUDAO_FREE.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

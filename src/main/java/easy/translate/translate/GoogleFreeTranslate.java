package easy.translate.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 谷歌免费文本翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/11/18 15:13:09
 */
public class GoogleFreeTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(GoogleFreeTranslate.class);

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.ZH_CN.lang, TranslateLanguageEnum.EN_US.lang);
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.EN_US.lang, TranslateLanguageEnum.ZH_CN.lang);
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/18 15:13
     */
    private String translate(String text, String source, String target) {
        try {
            String url = String.format(TranslateEnum.GOOGLE_FREE.getUrl(), source, target, URLEncoder.encode(text, StandardCharsets.UTF_8.name()));
            String response = HttpRequest.get(url)
                    .timeout(30000)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                    .header(Header.USER_AGENT, Constants.UA)
                    .execute().body();
            JsonObject resObject = JsonUtil.fromObject(response);
            JsonArray sentencesArray = resObject.getAsJsonArray("sentences");
            if (Objects.isNull(sentencesArray) || sentencesArray.isEmpty()) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (JsonElement element : sentencesArray) {
                JsonElement transElement = element.getAsJsonObject().get("trans");
                if (Objects.isNull(transElement)) {
                    continue;
                }
                String trans = transElement.getAsString();
                if (StringUtils.isBlank(trans)) {
                    continue;
                }
                builder.append(trans);
            }
            return builder.toString();
        } catch (Exception e) {
            log.error(TranslateEnum.GOOGLE.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return null;
    }

}

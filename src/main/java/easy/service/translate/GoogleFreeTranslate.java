package easy.service.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.service.AbstractTranslate;
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
        return translate(chStr, "zh_CN", "en_US");
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, "en_US", "zh_CN");
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
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0")
                    .execute().body();
            JsonObject resObject = JsonUtil.fromObject(response);
            JsonArray sentencesArray = resObject.getAsJsonArray("sentences");
            if (Objects.isNull(sentencesArray) || sentencesArray.isEmpty()) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (JsonElement element : sentencesArray) {
                JsonObject eleObject = element.getAsJsonObject();
                String trans = eleObject.get("trans").getAsString();
                if (StringUtils.isBlank(trans)) {
                    continue;
                }
                builder.append(trans);
            }
            return builder.toString();
        } catch (Exception e) {
            log.error(TranslateEnum.GOOGLE.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return null;
    }

}

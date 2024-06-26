package easy.translate.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 微软翻译免费API服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/11/20 14:19:07
 */
public class MicrosoftFreeTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(MicrosoftFreeTranslate.class);
    private static final String TOKEN_URL = "https://edge.microsoft.com/translate/auth";
    private static Cache<String, String> CACHE_TOKEN = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(3).build();
    private static final String TOKEN = "MICROSOFT_TOKEN";
    private static final int RETRY_COUNT = 3;

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.ZH_HANS.lang, TranslateLanguageEnum.EN.lang);
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.ZH_HANS.lang);
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/20 14:20
     */
    private String translate(String text, String source, String target) {
        try {
            String response = getTranslateResult(text, source, target);
            if (StringUtils.isNotBlank(response) && StringUtils.contains(response, "401000")) {
                getToken();
                response = getTranslateResult(text, source, target);
            }
            if (StringUtils.isBlank(response)) {
                return StringUtils.EMPTY;
            }
            JsonArray resArray = JsonUtil.fromArray(response);
            if (Objects.isNull(resArray) || resArray.isEmpty()) {
                return StringUtils.EMPTY;
            }
            JsonElement transElement = resArray.get(0).getAsJsonObject().get("translations");
            if (Objects.isNull(transElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement resultTransElement = transElement.getAsJsonArray().get(0);
            if (Objects.isNull(resultTransElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement textElement = resultTransElement.getAsJsonObject().get("text");
            if (Objects.isNull(textElement)) {
                return StringUtils.EMPTY;
            }
            return textElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.MICROSOFT_FREE.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 发起翻译请求
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/20 15:09
     */
    private String getTranslateResult(String text, String source, String target) {
        JsonObject dataObject = new JsonObject();
        dataObject.addProperty("Text", text);
        return HttpRequest.post(String.format(TranslateEnum.MICROSOFT_FREE.getUrl(), source, target))
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + getToken())
                .body(JsonUtil.toJson(Collections.singletonList(dataObject)))
                .execute().body();
    }

    /**
     * 获取token
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/20 15:09
     */
    private String getToken() {
        String token = CACHE_TOKEN.getIfPresent(TOKEN);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        // 尝试多次获取TOKEN (免费接口可能不稳定)
        String response = HttpUtil.get(TOKEN_URL);
        if (StringUtils.isBlank(response)) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                response = HttpUtil.get(TOKEN_URL);
                if (StringUtils.isNotBlank(response)) {
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(response)) {
            CACHE_TOKEN.put(TOKEN, response);
            token = response;
        }
        return token;
    }


}

package easy.translate.translate;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpStatus;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.enums.TranslateEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * CNKI学术翻译(逆向解析API翻译)
 *
 * @author mabin
 * @project EasyTool
 * @package easy.translate.translate
 * @date 2024/03/15 13:46
 */
public class CNKIFreeTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(CNKIFreeTranslate.class);
    private static final String TOKEN_URL = "https://dict.cnki.net/fyzs-front-api/getToken";
    private static Cache<String, String> CNKI_CACHE_TOKEN = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(2).build();
    private static final String TOKEN = "CNKI_TOKEN";
    private static final String AES_KEY = "4e87183cfd3a45fe";
    private static final int RETRY_COUNT = 3;

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, "0");
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, "1");
    }

    /**
     * 翻译处理
     *
     * @param text   文本
     * @param target 目标
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/15 13:51
     */
    private String translate(String text, String target) {
        try {
            String response = getTranslateResult(text, target);
            if (StringUtils.isBlank(response)) {
                return StringUtils.EMPTY;
            }
            if (JsonUtil.fromObject(response).get("code").getAsInt() == HttpStatus.HTTP_UNAUTHORIZED) {
                getToken();
                response = getTranslateResult(text, target);
                if (StringUtils.isBlank(response)) {
                    return StringUtils.EMPTY;
                }
            }
            JsonObject resObject = JsonUtil.fromObject(response);
            if (resObject.get("code").getAsInt() != HttpStatus.HTTP_OK) {
                return StringUtils.EMPTY;
            }
            JsonObject dataObject = resObject.get("data").getAsJsonObject();
            if (Objects.isNull(dataObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement jsonElement = dataObject.get("mResult");
            if (Objects.isNull(jsonElement)) {
                return StringUtils.EMPTY;
            }
            return jsonElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.CNKI.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取token令牌
     *
     * @return {@link String }
     * @author mabin
     * @date 2024/03/15 13:53
     */
    private String getToken() {
        String token = CNKI_CACHE_TOKEN.getIfPresent(TOKEN);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        Map<String, String> headersMap = new HashMap<>(3);
        headersMap.put(Header.USER_AGENT.getValue(), Constants.UA);
        String response = HttpUtil.doGetWithHeaders(TOKEN_URL, headersMap);
        if (StringUtils.isBlank(response)) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                response = HttpUtil.doGetWithHeaders(TOKEN_URL, headersMap);
                if (StringUtils.isNotBlank(response)) {
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(response)) {
            token = JsonUtil.fromObject(response).get("data").getAsString();
            CNKI_CACHE_TOKEN.put(TOKEN, token);
        }
        return token;
    }

    /**
     * 获取翻译结果
     *
     * @param text   文本
     * @param target 目标
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/15 13:52
     */
    private String getTranslateResult(String text, String target) {
        Map<String, String> paramsMap = new HashMap<>(5);
        paramsMap.put("translateType", target);
        paramsMap.put("words", getCipherWords(text));
        Map<String, String> headersMap = new HashMap<>(5);
        headersMap.put(Header.USER_AGENT.getValue(), Constants.UA);
        headersMap.put("Token", getToken());
        headersMap.put(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        return HttpUtil.doPost(TranslateEnum.CNKI.getUrl(), headersMap, JsonUtil.toJson(paramsMap));
    }

    /**
     * 获取待翻译文本密文
     *
     * @param text 文本
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/15 14:36
     */
    private String getCipherWords(String text) {
        String cipherText = SecureUtil.aes(AES_KEY.getBytes(StandardCharsets.UTF_8)).encryptBase64(text);
        cipherText = StringUtils.replace(cipherText, "+", "-");
        return StringUtils.replace(cipherText, "/", "_");
    }

}

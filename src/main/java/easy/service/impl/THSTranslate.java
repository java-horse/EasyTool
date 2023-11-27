package easy.service.impl;

import cn.hutool.http.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 同花顺翻译服务
 *
 * @project: EasyTool
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/11/27 16:39:54
 */
public class THSTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(THSTranslate.class);

    private static final String TOKEN_URL = "https://b2b-api.10jqka.com.cn/gateway/service-mana/app/login-appkey";
    private static final String CACHE_TOKEN_KEY = "THS_CACHE_TOKEN";
    private static Cache<String, String> THS_CACHE = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.HOURS).maximumSize(3).build();


    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, "auto", "en");
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, "auto", "zh");
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/27 16:49
     */
    private String translate(String text, String source, String target) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("app_id", getTranslateConfig().getThsAppId());
        paramsMap.put("text", JsonUtil.toJson(Collections.singletonList(text)));
        paramsMap.put("from", source);
        paramsMap.put("to", target);
        paramsMap.put("domain", "default");
        try (HttpResponse httpResponse = HttpRequest.post(TranslateEnum.THS_SOFT.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue())
                .header("open-authorization", "Bearer" + getToken())
                .form(paramsMap).execute()) {
            String body = httpResponse.body();
            return JsonUtil.fromObject(Objects.requireNonNull(body)).get("data").getAsJsonObject()
                    .get("trans_result").getAsJsonArray().get(0).getAsJsonObject().get("dst").getAsString();
        } catch (HttpException e) {
            log.error(TranslateEnum.THS_SOFT.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取token
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/27 16:49
     */
    private String getToken() {
        String token = THS_CACHE.getIfPresent(CACHE_TOKEN_KEY);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        TranslateConfig translateConfig = getTranslateConfig();
        try (HttpResponse httpResponse = HttpRequest.post(TOKEN_URL)
                .header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue())
                .form("appId", translateConfig.getThsAppId())
                .form("appSecret", translateConfig.getThsAppSecret())
                .execute()) {
            String response = httpResponse.body();
            log.warn("response=" + response);
            if (StringUtils.isNotBlank(response)) {
                String accessToken = JsonUtil.fromObject(response).get("data").getAsJsonObject().get("access_token").getAsString();
                if (StringUtils.isNotBlank(accessToken)) {
                    token = accessToken;
                    THS_CACHE.put(CACHE_TOKEN_KEY, token);
                    return token;
                }
            }
        } catch (Exception e) {
            log.error("获取token异常", e);
        }
        return token;
    }

}

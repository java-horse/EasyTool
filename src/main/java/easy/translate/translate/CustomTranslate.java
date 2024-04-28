package easy.translate.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(CustomTranslate.class);

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
        return translate(chStr, TranslateLanguageEnum.ZH_CN.lang, TranslateLanguageEnum.EN.lang);
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
        return translate(enStr, TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.ZH_CN.lang);
    }

    /**
     * 翻译
     *
     * @param text   文本
     * @param source 源
     * @param target 目标
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/28 17:00
     */
    private String translate(String text, String source, String target) {
        Map<String, Object> paramsMap = new HashMap<>(8);
        paramsMap.put("source_lang", source);
        paramsMap.put("target_lang", target);
        paramsMap.put("text", StringUtils.substring(text, 0 ,getTranslateConfig().getCustomApiMaxCharLength()));
        try (HttpResponse httpResponse = HttpRequest.post(TranslateEnum.CUSTOM.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(JsonUtil.toJson(paramsMap)).execute()) {
            String body = httpResponse.body();
            if (StringUtils.isBlank(body)) {
                return StringUtils.EMPTY;
            }
            JsonObject resObject = JsonUtil.fromObject(body);
            if (Objects.isNull(resObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement jsonElement = resObject.get("trans_result");
            if (Objects.isNull(jsonElement)) {
                return StringUtils.EMPTY;
            }
            return jsonElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.CUSTOM.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }


}

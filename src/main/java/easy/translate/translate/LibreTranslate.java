package easy.translate.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LibreTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(LibreTranslate.class);

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
        return translate(chStr, TranslateLanguageEnum.ZH.lang, TranslateLanguageEnum.EN.lang);
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
        return translate(enStr, TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.ZH.lang);
    }

    /**
     * 翻译处理
     *
     * @param text   文本
     * @param source 来源
     * @param target 目标
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/06 01:22
     */
    private String translate(String text, String source, String target) {
        try {
            TranslateConfig config = getTranslateConfig();
            Map<String, Object> paramMap = new HashMap<>(10);
            paramMap.put("q", text);
            paramMap.put("source", source);
            paramMap.put("target", target);
            paramMap.put("format", "text");
            String res = HttpUtil.doPost(Constants.HTTPS + config.getLibreServerUrl() + "/translate", paramMap);
            if (StringUtils.isBlank(res)) {
                return StringUtils.EMPTY;
            }
            JsonObject resObject = JsonUtil.fromObject(res);
            if (Objects.isNull(resObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement transElement = resObject.get("translatedText");
            if (Objects.isNull(transElement)) {
                return StringUtils.EMPTY;
            }
            return transElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.LIBRE.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

}

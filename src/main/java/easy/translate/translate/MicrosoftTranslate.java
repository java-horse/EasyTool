package easy.translate.translate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微软翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/10/08 14:27:57
 */
public class MicrosoftTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(MicrosoftTranslate.class);

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
        return translate(chStr, TranslateLanguageEnum.ZH_HANS.lang, TranslateLanguageEnum.EN.lang);
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
     * @date 2023/10/8 14:40
     */
    private String translate(String text, String source, String target) {
        try {
            JsonObject textObject = new JsonObject();
            textObject.addProperty("Text", text);
            JsonArray textArray = new JsonArray();
            textArray.add(textObject);
            Map<String, String> headersMap = new HashMap<>(3);
            headersMap.put("Ocp-Apim-Subscription-Key", getTranslateConfig().getMicrosoftKey());
            String res = HttpUtil.doPost(String.format(TranslateEnum.MICROSOFT.getUrl(), source, target), headersMap, JsonUtil.toJson(textArray));
            JsonArray resArray = JsonUtil.fromJson(res, JsonArray.class);
            if (Objects.isNull(resArray) || resArray.isEmpty()) {
                return StringUtils.EMPTY;
            }
            JsonElement transElement = resArray.get(0).getAsJsonObject().get("translations");
            if (Objects.isNull(transElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement resultElement = transElement.getAsJsonArray().get(0);
            if (Objects.isNull(resultElement)) {
                return  StringUtils.EMPTY;
            }
            JsonElement textElement = resultElement.getAsJsonObject().get("text");
            if (Objects.isNull(textElement)) {
                return StringUtils.EMPTY;
            }
            return textElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.MICROSOFT.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

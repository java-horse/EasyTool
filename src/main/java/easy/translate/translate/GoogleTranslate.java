package easy.translate.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 谷歌翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/10/08 09:44:21
 */
public class GoogleTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(GoogleTranslate.class);

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
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/10/8 9:46
     */
    private String translate(String text, String source, String target) {
        try {
            String res = HttpUtil.doGet(String.format(TranslateEnum.GOOGLE.getUrl(), URLEncoder.encode(text, StandardCharsets.UTF_8), source, target, getTranslateConfig().getGoogleSecretKey()));
            JsonObject resObject = JsonUtil.fromJson(res, JsonObject.class);
            JsonElement dataElement = resObject.get("data");
            if (Objects.isNull(dataElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement transElement = dataElement.getAsJsonObject().get("translations");
            if (Objects.isNull(transElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement resultElement = transElement.getAsJsonArray().get(0).getAsJsonObject()
                    .get("translatedText");
            if (Objects.isNull(resultElement)) {
                return StringUtils.EMPTY;
            }
            return resultElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.GOOGLE.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

}

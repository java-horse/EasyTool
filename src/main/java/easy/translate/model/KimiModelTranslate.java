package easy.translate.model;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonElement;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * kimi大模型翻译
 *
 * @author mabin
 * @project EasyTool
 * @package easy.translate.model
 * @date 2024/03/18 15:51
 */
public class KimiModelTranslate extends AbstractTranslate {
    private static final Logger log = Logger.getInstance(KimiModelTranslate.class);

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.ZH.desc, TranslateLanguageEnum.EN.desc);
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.EN.desc, TranslateLanguageEnum.ZH.desc);
    }

    /**
     * 翻译
     *
     * @param text   文本
     * @param source 来源
     * @param target 目标
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/18 15:52
     */
    private String translate(String text, String source, String target) {
        String bodyJson = String.format(OpenModelTranslateEnum.KIMI.getPrompt(), getTranslateConfig().getKimiModel(), source, target, target, target, text);
        try (HttpResponse httpResponse = HttpRequest.post(OpenModelTranslateEnum.KIMI.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + getTranslateConfig().getKimiKey())
                .body(bodyJson).execute()) {
            String response = httpResponse.body();
            if (StringUtils.isBlank(response)) {
                return StringUtils.EMPTY;
            }
            JsonElement choicesElement = JsonUtil.fromObject(response).get("choices");
            if (Objects.isNull(choicesElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement messageElement = choicesElement.getAsJsonArray().get(0).getAsJsonObject().get("message");
            if (Objects.isNull(messageElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement contentElement = messageElement.getAsJsonObject().get("content");
            if (Objects.isNull(contentElement)) {
                return StringUtils.EMPTY;
            }
            return replaceBackQuote(contentElement.getAsString());
        } catch (Exception e) {
            log.error(OpenModelTranslateEnum.TONG_YI.getModel() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

}

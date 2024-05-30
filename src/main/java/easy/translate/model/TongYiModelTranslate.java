package easy.translate.model;

import cn.hutool.http.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 通义千问大模型
 *
 * @project: EasyTool
 * @package: easy.service.model
 * @author: mabin
 * @date: 2023/11/28 13:42:41
 */
public class TongYiModelTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(TongYiModelTranslate.class);

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.ZH_CN.desc, TranslateLanguageEnum.EN.desc);
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.EN.desc, TranslateLanguageEnum.ZH_CN.desc);
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/28 13:44
     */
    private String translate(String text, String source, String target) {
        String bodyJson = String.format(OpenModelTranslateEnum.TONG_YI.getPrompt(), getTranslateConfig().getTyModel(), source, target, target, target, text);
        try (HttpResponse httpResponse = HttpRequest.post(OpenModelTranslateEnum.TONG_YI.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + getTranslateConfig().getTyKey())
                .body(bodyJson).execute()) {
            String response = httpResponse.body();
            if (StringUtils.isBlank(response)) {
                return StringUtils.EMPTY;
            }
            JsonObject resObject = JsonUtil.fromObject(response);
            if (Objects.isNull(resObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement outputElement = resObject.get("output");
            if (Objects.isNull(outputElement)) {
                return StringUtils.EMPTY;
            }
            JsonElement textElement = outputElement.getAsJsonObject().get("text");
            if (Objects.isNull(textElement)) {
                return StringUtils.EMPTY;
            }
            return replaceBackQuote(textElement.getAsString());
        } catch (Exception e) {
            log.error(OpenModelTranslateEnum.TONG_YI.getModel() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

}

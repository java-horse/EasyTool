package easy.translate.model;

import cn.hutool.http.*;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.enums.OpenModelTranslateEnum;
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
        return translate(chStr, "en");
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, "zh");
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
    private String translate(String text, String target) {
        JsonObject bodyObject = new JsonObject();
        bodyObject.addProperty("model", getTranslateConfig().getTyModel());
        JsonObject promptObject = new JsonObject();
        promptObject.addProperty("prompt", String.format(Constants.PROMPT_TEMPLATE, text, target));
        bodyObject.add("input", promptObject);
        try (HttpResponse httpResponse = HttpRequest.post(OpenModelTranslateEnum.TONG_YI.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.AUTHORIZATION, "Bearer " + getTranslateConfig().getTyKey())
                .body(JsonUtil.toJson(bodyObject)).execute()) {
            String response = httpResponse.body();
            return JsonUtil.fromObject(Objects.requireNonNull(response)).get("output").getAsJsonObject().get("text").getAsString();
        } catch (HttpException e) {
            log.error(OpenModelTranslateEnum.TONG_YI.getModel() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

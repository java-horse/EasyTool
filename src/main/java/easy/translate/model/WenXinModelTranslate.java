package easy.translate.model;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.JsonObject;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.base.ModelConstants;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 文心模型翻译
 *
 * @author mabin
 * @project EasyTool
 * @package easy.translate.model
 * @date 2024/03/19 15:12
 */
public class WenXinModelTranslate extends AbstractTranslate {
    private static final Logger log = Logger.getInstance(WenXinModelTranslate.class);
    private static final PropertiesComponent PROPERTIES_COMPONENT = PropertiesComponent.getInstance();
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, TranslateLanguageEnum.ZH.desc, TranslateLanguageEnum.EN.desc);
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr, TranslateLanguageEnum.EN.desc, TranslateLanguageEnum.ZH.desc);
    }

    /**
     * 翻译处理
     *
     * @param text   文本
     * @param source 来源
     * @param target 目标
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/18 15:52
     */
    private String translate(String text, String source, String target) {
        String bodyJson = String.format(OpenModelTranslateEnum.WEN_XIN.getPrompt(), target, target, text);
        try {
            String response = sendPost(bodyJson);
            if (StringUtils.isBlank(response)) {
                return StringUtils.EMPTY;
            }
            if (StringUtils.contains(response, "error_code")) {
                if (JsonUtil.fromObject(response).get("error_code").getAsInt() == 110) {
                    // 令牌置空，重新获取并请求
                    PROPERTIES_COMPONENT.setValue(Constants.Persistence.OPEN_MODEL.WEN_XIN_ACCESS_TOKEN, StringUtils.EMPTY);
                    response = sendPost(bodyJson);
                    if (StringUtils.isBlank(response)) {
                        return StringUtils.EMPTY;
                    }
                }
            }
            return replaceBackQuote(JsonUtil.fromObject(Objects.requireNonNull(response)).get("result").getAsString());
        } catch (Exception e) {
            log.error(OpenModelTranslateEnum.WEN_XIN.getModel() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 发送翻译请求
     *
     * @param bodyJson 请求Json参数
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/19 16:28
     */
    private String sendPost(String bodyJson) {
        try (HttpResponse httpResponse = HttpRequest.post(String.format(OpenModelTranslateEnum.WEN_XIN.getUrl(),
                        ModelConstants.WEN_XIN.getRealModel(getTranslateConfig().getWenxinModel()), getAccessToken()))
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(bodyJson)
                .execute()) {
            return httpResponse.body();
        } catch (Exception e) {
            throw new RuntimeException("获取访问令牌异常", e);
        }
    }

    /**
     * 获取访问令牌（有效期30天，所以适合进行持久化）
     *
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/19 15:21
     */
    private String getAccessToken() {
        String accessToken = PROPERTIES_COMPONENT.getValue(Constants.Persistence.OPEN_MODEL.WEN_XIN_ACCESS_TOKEN, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String response = HttpRequest.post(TOKEN_URL)
                .timeout(10000)
                .contentType(ContentType.JSON.getValue())
                .form("grant_type", "client_credentials")
                .form("client_id", getTranslateConfig().getWenxinApiKey())
                .form("client_secret", getTranslateConfig().getWenxinApiSecret())
                .execute().body();
        if (StringUtils.isNotBlank(response)) {
            JsonObject resObject = JsonUtil.fromObject(response);
            accessToken = resObject.get("access_token").getAsString();
            PROPERTIES_COMPONENT.setValue(Constants.Persistence.OPEN_MODEL.WEN_XIN_ACCESS_TOKEN, accessToken);
        }
        return accessToken;
    }

}

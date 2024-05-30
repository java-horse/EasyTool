package easy.translate.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 讯飞翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/09/28 10:04:12
 */
public class XfYunTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(XfYunTranslate.class);

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
        return translate(chStr, TranslateLanguageEnum.CN.lang, TranslateLanguageEnum.EN.lang);
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
        return translate(enStr, TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.CN.lang);
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/28 10:05
     */
    private String translate(String text, String source, String target) {
        try {
            String res = HttpUtil.doPost(buildRequestUrl(), buildParam(text, source, target));
            if (StringUtils.isBlank(res)) {
                return StringUtils.EMPTY;
            }
            JsonObject resObject = JsonUtil.fromJson(res, JsonObject.class);
            if (Objects.isNull(resObject)) {
                return StringUtils.EMPTY;
            }
            String decodeText = new String(Base64.getDecoder().decode(resObject.getAsJsonObject("payload")
                    .getAsJsonObject("result").get("text").getAsString()), StandardCharsets.UTF_8);
            JsonObject resultObject = JsonUtil.fromJson(decodeText, JsonObject.class);
            if (Objects.isNull(resultObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement dstElement = resultObject.getAsJsonObject("trans_result").get("dst");
            if (Objects.isNull(dstElement)) {
                return StringUtils.EMPTY;
            }
            return dstElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.XFYUN.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }


    /**
     * 组装带签名的URL
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/28 13:54
     */
    public String buildRequestUrl() throws Exception {
        TranslateConfig translateConfig = getTranslateConfig();
        URL url = new URL(TranslateEnum.XFYUN.getUrl());
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        String host = url.getHost();
        StringBuilder builder = new StringBuilder("host: ").append(host).append("\n").
                append("date: ").append(date).append("\n").
                append("POST ").append(url.getPath()).append(" HTTP/1.1");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(translateConfig.getXfApiSecret().getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", translateConfig.getXfApiKey(), "hmac-sha256", "host date request-line", sha);
        String authBase = Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
        return String.format("%s?authorization=%s&host=%s&date=%s", TranslateEnum.XFYUN.getUrl(), URLEncoder.encode(authBase, StandardCharsets.UTF_8.name()),
                URLEncoder.encode(host, StandardCharsets.UTF_8.name()), URLEncoder.encode(date, StandardCharsets.UTF_8.name()));
    }

    /**
     * 构建请求参数
     *
     * @param text
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/28 13:39
     */
    private String buildParam(String text, String source, String target) {
        TranslateConfig translateConfig = getTranslateConfig();
        JsonObject headerObject = new JsonObject();
        headerObject.addProperty("app_id", translateConfig.getXfAppId());
        headerObject.addProperty("status", 3);

        JsonObject parameterObject = new JsonObject();
        JsonObject itsObject = new JsonObject();
        itsObject.addProperty("from", source);
        itsObject.addProperty("to", target);
        itsObject.add("result", new JsonObject());
        parameterObject.add("its", itsObject);

        JsonObject payloadObject = new JsonObject();
        JsonObject inputDataObject = new JsonObject();
        inputDataObject.addProperty("status", 3);
        inputDataObject.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
        payloadObject.add("input_data", inputDataObject);

        JsonObject reqObject = new JsonObject();
        reqObject.add("header", headerObject);
        reqObject.add("parameter", parameterObject);
        reqObject.add("payload", payloadObject);
        return JsonUtil.toJson(reqObject);
    }

}

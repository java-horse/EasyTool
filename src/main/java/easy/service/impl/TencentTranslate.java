package easy.service.impl;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.net.HTTPMethod;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * 腾讯翻译服务
 */
public class TencentTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(TencentTranslate.class);

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
        return translate(chStr, "auto", "en");
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
     * @date 2023/9/20 15:24
     */
    private String translate(String text, String source, String target) {
        TranslateConfig translateConfig = getTranslateConfig();
        try {
            for (int i = 0; i < 10; i++) {
                SortedMap<String, String> params = new TreeMap<>();
                params.put("Nonce", String.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)));
                params.put("Timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                params.put("Region", "ap-beijing");
                params.put("SecretId", translateConfig.getTencentSecretId());
                params.put("Action", "TextTranslate");
                params.put("Version", "2018-03-21");
                params.put("SourceText", text);
                params.put("Source", source);
                params.put("Target", target);
                params.put("ProjectId", "0");

                String str2sign = getStringToSign(HTTPMethod.GET.name(), "tmt.tencentcloudapi.com", params);
                params.put("Signature", sign(str2sign, translateConfig.getTencentSecretKey(), "HmacSHA1"));
                String res = HttpUtil.doGet(TranslateEnum.TENCENT.getUrl(), params);
                TencentResult tencentResult = JsonUtil.fromJson(res, TencentResult.class);
                if (Objects.isNull(tencentResult) || Objects.isNull(tencentResult.getResponse()) || (Objects.nonNull(tencentResult.getResponse().getError())
                        && StringUtils.equals(tencentResult.getResponse().getError().getCode(), "RequestLimitExceeded"))) {
                    Thread.sleep(500);
                } else {
                    return tencentResult.getResponse().getTargetText();
                }
            }
        } catch (Exception e) {
            log.error(TranslateEnum.TENCENT.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 签名处理
     *
     * @param s
     * @param key
     * @param method
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/20 21:29
     */
    private String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 组装签名字符串
     *
     * @param method
     * @param endpoint
     * @param params
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/20 21:29
     */
    private String getStringToSign(String method, String endpoint, SortedMap<String, String> params) {
        StringBuilder s2s = new StringBuilder();
        s2s.append(method).append(endpoint).append("/?");
        for (Map.Entry<String, String> e : params.entrySet()) {
            s2s.append(e.getKey()).append("=").append(params.get(e.getKey())).append("&");
        }
        return s2s.substring(0, s2s.length() - 1);
    }

    private static class TencentResult {
        @SerializedName("Response")
        private TencentResponse response;

        public TencentResponse getResponse() {
            return response;
        }

        public void setResponse(TencentResponse response) {
            this.response = response;
        }

        @Override
        public String toString() {
            return "TencentResult{" +
                    "response=" + response +
                    '}';
        }
    }

    private static class TencentResponse {
        @SerializedName("RequestId")
        private String requestId;
        @SerializedName("Source")
        private String source;
        @SerializedName("Target")
        private String target;
        @SerializedName("TargetText")
        private String targetText;
        @SerializedName("Error")
        private TencentError error;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTargetText() {
            return targetText;
        }

        public void setTargetText(String targetText) {
            this.targetText = targetText;
        }

        public TencentError getError() {
            return error;
        }

        public void setError(TencentError error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "TencentResponse{" +
                    "requestId='" + requestId + '\'' +
                    ", source='" + source + '\'' +
                    ", target='" + target + '\'' +
                    ", targetText='" + targetText + '\'' +
                    ", error=" + error +
                    '}';
        }
    }

    private static class TencentError {
        @SerializedName("Code")
        private String code;
        @SerializedName("Message")
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "TencentError{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

}

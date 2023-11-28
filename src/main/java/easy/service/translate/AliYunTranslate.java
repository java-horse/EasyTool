package easy.service.translate;

import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.service.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云翻译服务
 *
 * @author mabin
 * @date 2023/9/18 14:44
 */
public class AliYunTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(AliYunTranslate.class);

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
     * @date 2023/9/18 14:45
     */
    private String translate(String text, String source, String target) {
        try {
            Map<String, Object> paramsMap = new HashMap<>(16);
            paramsMap.put("SourceText", text);
            paramsMap.put("SourceLanguage", source);
            paramsMap.put("TargetLanguage", target);
            paramsMap.put("FormatType", "text");
            paramsMap.put("Scene", "general");
            TranslateConfig translateConfig = getTranslateConfig();
            String res = sendPost(TranslateEnum.ALIYUN.getUrl(), paramsMap, translateConfig.getAccessKeyId(), translateConfig.getAccessKeySecret());
            AliYunResponseVO responseVO = JsonUtil.fromJson(res, AliYunResponseVO.class);
            return Objects.requireNonNull(responseVO).getData().getTranslated();
        } catch (Exception e) {
            log.error(TranslateEnum.ALIYUN.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 计算 HMAC-SHA1
     *
     * @param data
     * @param key
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/19 17:50
     */
    private String hmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac).trim();
    }

    /**
     * 获取时间
     *
     * @param date
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/19 17:51
     */
    private String toGMTString(Date date) {
        SimpleDateFormat df1 = new SimpleDateFormat("E, dd ", Locale.UK);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM", Locale.UK);
        SimpleDateFormat df3 = new SimpleDateFormat(" yyyy HH:mm:ss z", Locale.UK);
        df1.setTimeZone(new SimpleTimeZone(0, "GMT"));
        df2.setTimeZone(new SimpleTimeZone(0, "GMT"));
        df3.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String month = df2.format(date);
        if (month.length() > 3) {
            month = month.substring(0, 3);
        }
        return df1.format(date) + month + df3.format(date);
    }

    /**
     * 发送翻译请求
     *
     * @param url
     * @param paramsMap
     * @param akId
     * @param akSecret
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/19 17:51
     */
    private String sendPost(String url, Map<String, Object> paramsMap, String akId, String akSecret) throws Exception {
        URL realUrl = new URL(url);
        String method = "POST";
        String accept = "application/json";
        String contentType = "application/json;charset=utf-8";
        String path = realUrl.getFile();
        String date = toGMTString(new Date());
        String host = realUrl.getHost();
        String paramJson = JsonUtil.toJson(paramsMap);
        String bodyMd5 = Base64.getEncoder().encodeToString(DigestUtils.md5(paramJson));
        String uuid = UUID.randomUUID().toString();
        String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + contentType + "\n" + date + "\n"
                + "x-acs-signature-method:HMAC-SHA1\n"
                + "x-acs-signature-nonce:" + uuid + "\n"
                + "x-acs-version:2019-01-02\n"
                + path;
        String signature = hmacSha1(stringToSign, akSecret);
        String authHeader = "acs " + akId + ":" + signature;

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", accept);
        headers.put("Content-Type", contentType);
        headers.put("Content-MD5", bodyMd5);
        headers.put("Date", date);
        headers.put("Host", host);
        headers.put("Authorization", authHeader);
        headers.put("x-acs-signature-nonce", uuid);
        headers.put("x-acs-signature-method", "HMAC-SHA1");
        headers.put("x-acs-version", "2019-01-02");
        return HttpUtil.doPost(url, headers, paramJson);
    }

    /**
     * 响应实例
     */
    private static class AliYunResponseVO {
        @SerializedName("Code")
        private String code;
        @SerializedName("RequestId")
        private String requestId;
        @SerializedName("Data")
        private AliYunResponseDataVO data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public AliYunResponseDataVO getData() {
            return data;
        }

        public void setData(AliYunResponseDataVO data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "AliYunResponseVO{" +
                    "code='" + code + '\'' +
                    ", requestId='" + requestId + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    private static class AliYunResponseDataVO {
        @SerializedName("WordCount")
        private String wordCount;
        @SerializedName("Translated")
        private String translated;

        public String getWordCount() {
            return wordCount;
        }

        public void setWordCount(String wordCount) {
            this.wordCount = wordCount;
        }

        public String getTranslated() {
            return translated;
        }

        public void setTranslated(String translated) {
            this.translated = translated;
        }

        @Override
        public String toString() {
            return "AliYunResponseDataVO{" +
                    "wordCount='" + wordCount + '\'' +
                    ", translated='" + translated + '\'' +
                    '}';
        }
    }

}

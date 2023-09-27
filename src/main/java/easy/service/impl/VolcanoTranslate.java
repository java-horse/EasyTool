package easy.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 火山翻译
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/09/27 09:53:53
 */
public class VolcanoTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(VolcanoTranslate.class);

    private static final BitSet URLENCODER = new BitSet(256);
    private static final String CONST_ENCODE = "0123456789ABCDEF";

    static {
        int i;
        for (i = 97; i <= 122; ++i) {
            URLENCODER.set(i);
        }

        for (i = 65; i <= 90; ++i) {
            URLENCODER.set(i);
        }

        for (i = 48; i <= 57; ++i) {
            URLENCODER.set(i);
        }
        URLENCODER.set('-');
        URLENCODER.set('_');
        URLENCODER.set('.');
        URLENCODER.set('~');
    }

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
        return translate(chStr, "en");
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
        return translate(enStr, "zh");
    }

    /**
     * 翻译处理
     * AKLTNTQ3YTg4MTdmNmRjNGFkMjkwZmQ2M2RhZDQ0NThhMTU
     * TVdKbU5qSmhaamRrWWpZNU5ERTRNemszWmpnMlpXTXhNV0l3WldZME5tVQ==
     *
     * @param text
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/27 10:05
     */
    private String translate(String text, String target) {
        try {
            Map<String, Object> paramsMap = new HashMap<>(16);
            paramsMap.put("TargetLanguage", target);
            paramsMap.put("TextList", Lists.newArrayList(text));
            TranslateConfig translateConfig = getTranslateConfig();
            String res = sendPost(TranslateEnum.VOLCANO.getUrl(), paramsMap, translateConfig.getVolcanoSecretId(), translateConfig.getVolcanoSecretKey());
            log.warn("res=" + res);
            VolcanoResponse responseVo = JsonUtil.fromJson(res, VolcanoResponse.class);
            log.warn("responseVo=" + responseVo);
            return Objects.requireNonNull(responseVo).getTranslateList().get(0).getTranslation();
        } catch (Exception e) {
            log.error("请求火山云翻译接口异常：请检查本地网络是否可连接外网，也有可能被火山云限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 发送翻译请求
     *
     * @param url
     * @param paramsMap
     * @param volcanoSecretId
     * @param volcanoSecretKey
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/27 10:33
     */
    private String sendPost(String url, Map<String, Object> paramsMap, String volcanoSecretId, String volcanoSecretKey) throws Exception {
        URL realUrl = new URL(url);
        String path = "/";
        String host = realUrl.getHost();
        String region = "cn-north-1";
        String service = "translate";
        String method = "POST";

        String xContentSha256 = hashSHA256(JsonUtil.toJson(paramsMap).getBytes());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String xDate = sdf.format(new Date());
        String shortXDate = xDate.substring(0, 8);
        String contentType = "application/x-www-form-urlencoded";
        String signHeader = "host;x-date;x-content-sha256;content-type";

        SortedMap<String, String> realQueryList = new TreeMap<>();
        realQueryList.put("Action", "TranslateText");
        realQueryList.put("Version", "2020-06-01");
        StringBuilder querySB = new StringBuilder();
        for (Map.Entry<String, String> entry : realQueryList.entrySet()) {
            querySB.append(signStringEncoder(entry.getKey()))
                    .append("=")
                    .append(signStringEncoder(entry.getValue()))
                    .append("&");
        }
        querySB.deleteCharAt(querySB.length() - 1);

        String canonicalStringBuilder = method + "\n" + path + "\n" + querySB + "\n" +
                "host:" + host + "\n" +
                "x-date:" + xDate + "\n" +
                "x-content-sha256:" + xContentSha256 + "\n" +
                "content-type:" + contentType + "\n" +
                "\n" +
                signHeader + "\n" +
                xContentSha256;

        String hashcanonicalString = hashSHA256(canonicalStringBuilder.getBytes());
        String credentialScope = shortXDate + "/" + region + "/" + service + "/request";
        String signString = "HMAC-SHA256" + "\n" + xDate + "\n" + credentialScope + "\n" + hashcanonicalString;

        byte[] signKey = genSigningSecretKeyV4(volcanoSecretKey, shortXDate, region, service);
        String signature = HexFormat.of().formatHex(hmacSHA256(signKey, signString));

        Map<String, String> headersMap = new HashMap<>(16);
        headersMap.put("Host", host);
        headersMap.put("X-Date", xDate);
        headersMap.put("X-Content-Sha256", xContentSha256);
        headersMap.put("Content-Type", contentType);
        headersMap.put("Authorization", "HMAC-SHA256" +
                " Credential=" + volcanoSecretId + "/" + credentialScope +
                ", SignedHeaders=" + signHeader +
                ", Signature=" + signature);
         return HttpUtil.doPost(url + "?" + querySB, headersMap, paramsMap, Boolean.FALSE);
    }


    private String signStringEncoder(String source) {
        if (source == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(source.length());
        ByteBuffer bb = StandardCharsets.UTF_8.encode(source);
        while (bb.hasRemaining()) {
            int b = bb.get() & 255;
            if (URLENCODER.get(b)) {
                buf.append((char) b);
            } else if (b == 32) {
                buf.append("%20");
            } else {
                buf.append("%");
                char hex1 = CONST_ENCODE.charAt(b >> 4);
                char hex2 = CONST_ENCODE.charAt(b & 15);
                buf.append(hex1);
                buf.append(hex2);
            }
        }
        return buf.toString();
    }

    public static String hashSHA256(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(md.digest(content));
    }

    public static byte[] hmacSHA256(byte[] key, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(content.getBytes());
    }

    private byte[] genSigningSecretKeyV4(String secretKey, String date, String region, String service) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] kDate = hmacSHA256((secretKey).getBytes(), date);
        byte[] kRegion = hmacSHA256(kDate, region);
        byte[] kService = hmacSHA256(kRegion, service);
        return hmacSHA256(kService, "request");
    }


    public static class VolcanoResponse {
        @SerializedName("TranslateList")
        private List<TranslateList> translateList;

        public List<TranslateList> getTranslateList() {
            return translateList;
        }

        public void setTranslateList(List<TranslateList> translateList) {
            this.translateList = translateList;
        }

        @Override
        public String toString() {
            return "VolcanoResponse{" +
                    "translateList=" + translateList +
                    '}';
        }
    }

    public static class TranslateList {
        @SerializedName("Translation")
        private String translation;
        @SerializedName("DetectedSourceLanguage")
        private String detectedSourceLanguage;

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }

        public String getDetectedSourceLanguage() {
            return detectedSourceLanguage;
        }

        public void setDetectedSourceLanguage(String detectedSourceLanguage) {
            this.detectedSourceLanguage = detectedSourceLanguage;
        }

        @Override
        public String toString() {
            return "TranslateList{" +
                    "translation='" + translation + '\'' +
                    ", detectedSourceLanguage='" + detectedSourceLanguage + '\'' +
                    '}';
        }
    }

}

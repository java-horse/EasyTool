package easy.service.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.service.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * 华为云翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/11/18 11:27:35
 */
public class HuaWeiTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(HuaWeiTranslate.class);

    private static final String URL_PAR = "/v1/%s/machine-translation/text-translation/\n";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final String INTERNATIONAL_PROTOCOL = "TLSv1.2";

    static {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr, "auto", "en");
    }

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
     * @date 2023/11/18 11:28
     */
    private String translate(String text, String source, String target) {
        try {
            JsonObject bodyObject = new JsonObject();
            bodyObject.addProperty("text", text);
            bodyObject.addProperty("from", source);
            bodyObject.addProperty("to", target);
            String body = JsonUtil.toJson(bodyObject);
            String date = SIMPLE_DATE_FORMAT.format(new Date());
            String signatureResult = computeAuthorization(body, date);
            String response = HttpRequest.post(String.format(TranslateEnum.HUAWEI.getUrl(), getTranslateConfig().getHwProjectId()))
                    .timeout(10000)
                    .header(Header.AUTHORIZATION, signatureResult)
                    .header(Header.HOST, "nlp-ext.cn-north-4.myhuaweicloud.com")
                    .header("X-Sdk-Date", date)
                    .contentType(ContentType.JSON.getValue())
                    .setSSLProtocol(INTERNATIONAL_PROTOCOL)
                    .body(body)
                    .execute().body();
            JsonObject resObject = JsonUtil.fromObject(response);
            return Objects.requireNonNull(resObject.get("translated_text")).getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.HUAWEI.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return null;
    }

    private String computeAuthorization(String body, String date) {
        String messageDigestContent = toHex(hash(body));
        TranslateConfig translateConfig = getTranslateConfig();
        String canonicalRequest = "POST\n" +
                String.format(URL_PAR, translateConfig.getHwProjectId()) +
                "\n" +
                "content-type:application/json\n" +
                "host:nlp-ext.cn-north-4.myhuaweicloud.com\n" +
                String.format("x-sdk-date:%s\n", date) +
                "\n" +
                "content-type;host;x-sdk-date\n" +
                messageDigestContent;
        String stringToSign = "SDK-HMAC-SHA256" + "\n" + date + "\n" + toHex(hash(canonicalRequest));
        byte[] signature = sign(stringToSign.getBytes(StandardCharsets.UTF_8), translateConfig.getHwAppSecret().getBytes(StandardCharsets.UTF_8));
        String credential = "Access=" + translateConfig.getHwAppId();
        String signerHeaders = "SignedHeaders=" + "content-type;host;x-sdk-date";
        String signatureHeader = "Signature=" + toHex(signature);
        return "SDK-HMAC-SHA256" + StringUtils.SPACE + credential + ", " + signerHeaders + ", " + signatureHeader;
    }

    private String toHex(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);
        byte[] var2 = data;
        int var3 = data.length;
        for (int var4 = 0; var4 < var3; ++var4) {
            byte bye = var2[var4];
            String hexStr = Integer.toHexString(bye);
            if (hexStr.length() == 1) {
                builder.append("0");
            } else if (hexStr.length() == 8) {
                hexStr = hexStr.substring(6);
            }
            builder.append(hexStr);
        }
        return builder.toString().toLowerCase(Locale.getDefault());
    }

    private byte[] sign(byte[] data, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException var5) {
            return new byte[0];
        }
    }

    private byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException var3) {
            return new byte[0];
        }
    }

}

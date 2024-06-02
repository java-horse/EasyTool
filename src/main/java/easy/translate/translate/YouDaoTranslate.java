package easy.translate.translate;

import cn.hutool.core.collection.CollUtil;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.enums.YouDaoTranslateDomainEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 有道智云AI翻译
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/09/21 19:16:23
 */
public class YouDaoTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(YouDaoTranslate.class);

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
        return translate(chStr, TranslateLanguageEnum.AUTO.lang, TranslateLanguageEnum.EN.lang);
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
        return translate(enStr, TranslateLanguageEnum.AUTO.lang, TranslateLanguageEnum.ZH_CHS.lang);
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/21 19:19
     */
    private String translate(String text, String source, String target) {
        TranslateConfig translateConfig = getTranslateConfig();
        try {
            Map<String, String> params = new HashMap<>(16);
            String salt = Long.toString(System.currentTimeMillis());
            String currentTime = Long.toString(System.currentTimeMillis() / 1000);
            params.put("from", source);
            params.put("to", target);
            params.put("q", text);
            params.put("signType", "v3");
            params.put("curtime", currentTime);
            params.put("appKey", translateConfig.getSecretId());
            params.put("domain", YouDaoTranslateDomainEnum.GENERAL.getDomain());
            params.put("salt", salt);
            params.put("sign", sign(translateConfig.getSecretId() + truncate(text) + salt + currentTime + translateConfig.getSecretKey()));
            // 是否领域翻译
            if (Boolean.TRUE.equals(translateConfig.getYoudaoDomainCheckBox()) && StringUtils.isNotBlank(translateConfig.getYoudaoDomainComboBox())) {
                String domain = YouDaoTranslateDomainEnum.getDomain(translateConfig.getYoudaoDomainComboBox());
                if (StringUtils.isNotBlank(domain)) {
                    params.put("domain", domain);
                }
            }
            String res = HttpUtil.doGet(TranslateEnum.YOUDAO.getUrl(), params);
            if (StringUtils.isBlank(res)) {
                return StringUtils.EMPTY;
            }
            YouDaoAiResponse response = JsonUtil.fromJson(res, YouDaoAiResponse.class);
            if (Objects.isNull(response)) {
                return StringUtils.EMPTY;
            }
            List<String> translationList = response.getTranslation();
            if (CollUtil.isEmpty(translationList)) {
                return StringUtils.EMPTY;
            }
            return translationList.get(0);
        } catch (Exception e) {
            log.error(TranslateEnum.YOUDAO.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 签名
     *
     * @param string
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/21 19:26
     */
    public static String sign(String string) {
        if (string == null) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 字符串截断处理
     *
     * @param q
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/21 19:27
     */
    public static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    private static class YouDaoAiResponse {
        private String errorCode;
        private List<String> translation;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public List<String> getTranslation() {
            return translation;
        }

        public void setTranslation(List<String> translation) {
            this.translation = translation;
        }

        @Override
        public String toString() {
            return "YouDaoAiResponse{" +
                    "errorCode='" + errorCode + '\'' +
                    ", translation=" + translation +
                    '}';
        }
    }

}

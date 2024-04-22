package easy.translate.translate;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.diagnostic.Logger;
import easy.config.translate.TranslateConfig;
import easy.enums.BaiDuTranslateDomainEnum;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 百度翻译服务
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 17:34
 **/

public class BaiDuTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(BaiDuTranslate.class);


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
        return translate(enStr, TranslateLanguageEnum.AUTO.lang, TranslateLanguageEnum.ZH.lang);
    }

    /**
     * 中英互译
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:56
     **/
    private String translate(String text, String source, String target) {
        TranslateConfig translateConfig = getTranslateConfig();
        String appId = translateConfig.getAppId();
        if (StringUtils.isNoneBlank(text, appId, translateConfig.getAppSecret())) {
            return StringUtils.EMPTY;
        }
        try {
            for (int i = 0; i < 3; i++) {
                Map<String, Object> paramsMap = new HashMap<>(16);
                paramsMap.put("q", text);
                paramsMap.put("from", source);
                paramsMap.put("to", target);

                paramsMap.put("appid", appId);
                String salt = Long.toString(System.currentTimeMillis());
                paramsMap.put("salt", salt);
                // 是否领域翻译
                String url = TranslateEnum.BAIDU.getUrl();
                if (Boolean.TRUE.equals(translateConfig.getBaiduDomainCheckBox()) && StringUtils.isNotBlank(translateConfig.getBaiduDomainComboBox())) {
                    String domain = BaiDuTranslateDomainEnum.getDomain(translateConfig.getBaiduDomainComboBox());
                    if (StringUtils.isNotBlank(domain)) {
                        paramsMap.put("domain", domain);
                        url = TranslateEnum.BAIDU.getDomainUrl();
                    }
                }
                Object domainStr = paramsMap.get("domain");
                paramsMap.put("sign", DigestUtils.md5Hex(appId + text + salt + (Objects.isNull(domainStr) ? StringUtils.EMPTY : domainStr) + translateConfig.getAppSecret()));
                Map<String, String> headersMap = new HashMap<>(3);
                headersMap.put("Content-Type", "application/x-www-form-urlencoded");
                String res = HttpUtil.doPost(url, headersMap, paramsMap);
                if (StringUtils.isBlank(res)) {
                    return StringUtils.EMPTY;
                }
                BaiduResponse baiduResponse = JsonUtil.fromJson(res, BaiduResponse.class);
                if (Objects.isNull(baiduResponse) || "54003".equals(baiduResponse.getErrorCode())) {
                    Thread.sleep(500);
                } else {
                    List<TransResult> transResultList = baiduResponse.getTransResult();
                    if (CollUtil.isEmpty(transResultList)) {
                        return StringUtils.EMPTY;
                    }
                    return transResultList.get(0).getDst();
                }
            }
        } catch (Exception e) {
            log.error(TranslateEnum.BAIDU.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 响应实例
     */
    private static class BaiduResponse {
        @SerializedName("error_code")
        private String errorCode;
        @SerializedName("error_msg")
        private String errorMsg;
        private String from;
        private String to;
        @SerializedName("trans_result")
        private List<TransResult> transResult;

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFrom() {
            return from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTo() {
            return to;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public List<TransResult> getTransResult() {
            return transResult;
        }

        public void setTransResult(List<TransResult> transResult) {
            this.transResult = transResult;
        }

        @Override
        public String toString() {
            return "BaiduResponse{" +
                    "errorCode='" + errorCode + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    ", from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", transResult=" + transResult +
                    '}';
        }
    }

    private static class TransResult {
        private String src;
        private String dst;

        public void setSrc(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getDst() {
            return dst;
        }

        @Override
        public String toString() {
            return "TransResult{" +
                    "src='" + src + '\'' +
                    ", dst='" + dst + '\'' +
                    '}';
        }
    }

}

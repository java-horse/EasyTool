package easy.service.impl;

import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
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
        try {
            return translate("zh", "en", chStr);
        } catch (Exception e) {
            log.error("请求百度翻译接口异常：请检查本地网络是否可连接外网，也有可能被百度限流", e);
        }
        return StringUtils.EMPTY;
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
        try {
            return translate("en", "zh", enStr);
        } catch (Exception e) {
            log.error("请求百度翻译接口异常：请检查本地网络是否可连接外网，也有可能被百度限流", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 中英互译
     *
     * @param sourceLanguage
     * @param targetLanguage
     * @param text
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:56
     **/
    private String translate(String sourceLanguage, String targetLanguage, String text) throws InterruptedException {
        if (StringUtils.isAnyBlank(sourceLanguage, targetLanguage, text)) {
            return StringUtils.EMPTY;
        }
        for (int i = 0; i < 10; i++) {
            Map<String, String> paramsMap = new HashMap<>(16);
            paramsMap.put("q", text);
            paramsMap.put("from", sourceLanguage);
            paramsMap.put("to", targetLanguage);
            String appId = getTranslateConfig().getAppId();
            paramsMap.put("appid", appId);
            String salt = Long.toString(System.currentTimeMillis());
            paramsMap.put("salt", salt);
            paramsMap.put("sign", DigestUtils.md5Hex(appId + text + salt + getTranslateConfig().getAppSecret()));
            Map<String, String> headersMap = new HashMap<>(3);
            headersMap.put("Content-Type", "application/x-www-form-urlencoded");
            String res = HttpUtil.doPost(TranslateEnum.BAIDU.getUrl(), headersMap, paramsMap, Boolean.FALSE);
            if (StringUtils.isBlank(res)) {
                return StringUtils.EMPTY;
            }
            BaiduResponse baiduResponse = JsonUtil.fromJson(res, BaiduResponse.class);
            if (Objects.isNull(baiduResponse) || "54003".equals(baiduResponse.getError_code())) {
                Thread.sleep(500);
            } else {
                return baiduResponse.getTrans_result().get(0).getDst();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 响应实例
     */
    private static class BaiduResponse {
        private String error_code;
        private String error_msg;
        private String from;
        private String to;
        private List<TransResult> trans_result;

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

        public String getError_code() {
            return error_code;
        }

        public void setError_code(String error_code) {
            this.error_code = error_code;
        }

        public String getError_msg() {
            return error_msg;
        }

        public void setError_msg(String error_msg) {
            this.error_msg = error_msg;
        }

        public List<TransResult> getTrans_result() {
            return trans_result;
        }

        public void setTrans_result(List<TransResult> trans_result) {
            this.trans_result = trans_result;
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

        @Override
        public String toString() {
            return "BaiduResponse{" +
                    "error_code='" + error_code + '\'' +
                    ", error_msg='" + error_msg + '\'' +
                    ", from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", trans_result=" + trans_result +
                    '}';
        }
    }

}

package easy.translate.translate;

import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.translate.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 彩云翻译
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/11/01 17:33:06
 */
public class CaiYunTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(CaiYunTranslate.class);

    @Override
    protected String translateCh2En(String chStr) {
        return translate(chStr,"auto", "en");
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return translate(enStr,"auto", "zh");
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/1 17:34
     */
    private String translate(String text, String source, String target) {
        try {
            JsonObject dataObject = new JsonObject();
            dataObject.addProperty("source", text);
            dataObject.addProperty("trans_type", source.concat("2").concat(target));
            dataObject.addProperty("request_id", System.currentTimeMillis());
            dataObject.addProperty("detect", true);

            Map<String, String> headersMap = new HashMap<>(6);
            headersMap.put("Content-Type", "application/json;charset=utf-8");
            headersMap.put("x-authorization", "token " + getTranslateConfig().getCaiyunToken());
            String res = HttpUtil.doPost(TranslateEnum.CAIYUN.getUrl(), headersMap, JsonUtil.toJson(dataObject));
            return JsonUtil.fromObject(res).get("target").getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.CAIYUN.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

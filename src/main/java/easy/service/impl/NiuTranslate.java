package easy.service.impl;

import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 小牛翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/10/09 09:49:31
 */
public class NiuTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(NiuTranslate.class);

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
        return translate(chStr, "zh", "en");
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
        return translate(enStr, "en", "zh");
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/10/9 15:13
     */
    private String translate(String text, String source, String target) {
        try {
            String res = HttpUtil.doGet(String.format(TranslateEnum.NIU.getUrl(), source, target, getTranslateConfig().getNiuApiKey(), URLEncoder.encode(text, StandardCharsets.UTF_8.name())));
            JsonObject resObject = JsonUtil.fromJson(res, JsonObject.class);
            return Objects.requireNonNull(resObject).get("tgt_text").getAsString();
        } catch (Exception e) {
            log.error("请求小牛翻译接口异常：请检查本地网络是否可连接外网，也有可能被小牛翻译服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

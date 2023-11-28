package easy.service.translate;

import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.service.AbstractTranslate;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 谷歌翻译服务
 *
 * @project: EasyChar
 * @package: easy.service.impl
 * @author: mabin
 * @date: 2023/10/08 09:44:21
 */
public class GoogleTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(GoogleTranslate.class);

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
     * @date 2023/10/8 9:46
     */
    private String translate(String text, String source, String target) {
        try {
            String res = HttpUtil.doGet(String.format(TranslateEnum.GOOGLE.getUrl(), URLEncoder.encode(text, StandardCharsets.UTF_8.name()), source, target, getTranslateConfig().getGoogleSecretKey()));
            JsonObject resObject = JsonUtil.fromJson(res, JsonObject.class);
            return resObject.get("data").getAsJsonObject().get("translations").getAsJsonArray().get(0).getAsJsonObject()
                    .get("translatedText").getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.GOOGLE.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

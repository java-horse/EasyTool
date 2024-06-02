package easy.translate.translate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.AbstractTranslate;
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
        return translate(chStr, TranslateLanguageEnum.ZH.lang, TranslateLanguageEnum.EN.lang);
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
        return translate(enStr, TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.ZH.lang);
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
            String res = HttpUtil.doGet(String.format(TranslateEnum.NIU.getUrl(), source, target, getTranslateConfig().getNiuApiKey(), URLEncoder.encode(text, StandardCharsets.UTF_8)));
            if (StringUtils.isBlank(res)) {
                return StringUtils.EMPTY;
            }
            JsonObject resObject = JsonUtil.fromObject(res);
            if (Objects.isNull(resObject)) {
                return StringUtils.EMPTY;
            }
            JsonElement textElement = resObject.get("tgt_text");
            if (Objects.isNull(textElement)) {
                return StringUtils.EMPTY;
            }
            return textElement.getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.NIU.getTranslate() + "接口异常: 网络超时或被渠道服务限流, 请稍后重试", e);
        }
        return StringUtils.EMPTY;
    }

}

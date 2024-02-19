package easy.service.translate;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.intellij.openapi.diagnostic.Logger;
import easy.enums.TranslateEnum;
import easy.service.AbstractTranslate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 金山词霸翻译服务
 *
 * @author 马滨
 * @date 2023/11/19 16:23
 **/

public class KingSoftTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(KingSoftTranslate.class);

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
     * @return
     */
    private String translate(String text, String source, String target) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("from", source);
        paramsMap.put("to", target);
        paramsMap.put("q", text);
        try (HttpResponse httpResponse = HttpRequest.post(TranslateEnum.KING_SOFT.getUrl())
                .timeout(10000)
                .header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue())
                .form(paramsMap).execute()) {
            String body = httpResponse.body();
            return JsonUtil.fromObject(Objects.requireNonNull(body)).get("out").getAsString();
        } catch (Exception e) {
            log.error(TranslateEnum.KING_SOFT.getTranslate() + "接口异常: 网络超时或被渠道服务限流", e);
        }
        return StringUtils.EMPTY;
    }

}

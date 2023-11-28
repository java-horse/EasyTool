package easy.service.model;

import com.intellij.openapi.diagnostic.Logger;
import easy.service.AbstractTranslate;

/**
 * 文心一言千帆大模型翻译
 *
 * @project: EasyTool
 * @package: easy.service.model
 * @author: mabin
 * @date: 2023/11/28 13:54:40
 */
public class WenXinModelTranslate extends AbstractTranslate {

    private static final Logger log = Logger.getInstance(WenXinModelTranslate.class);

    @Override
    protected String translateCh2En(String chStr) {
        return null;
    }

    @Override
    protected String translateEn2Ch(String enStr) {
        return null;
    }

    /**
     * 翻译处理
     *
     * @param text
     * @param source
     * @param target
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/28 13:44
     */
    private String translate(String text, String source, String target) {

        return null;
    }

}

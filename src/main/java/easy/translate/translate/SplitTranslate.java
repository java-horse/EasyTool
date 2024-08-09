package easy.translate.translate;

import easy.translate.AbstractTranslate;
import easy.util.LanguageUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 拆分转换
 *
 * @author mabin
 * @project EasyTool
 * @package easy.translate.translate
 * @date 2024/08/09 10:00
 */
public class SplitTranslate extends AbstractTranslate {

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
        return String.join(StringUtils.SPACE, LanguageUtil.splitWord(chStr));
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
        return String.join(StringUtils.SPACE, LanguageUtil.splitWord(enStr));
    }

}

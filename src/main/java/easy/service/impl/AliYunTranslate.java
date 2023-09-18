package easy.service.impl;

/**
 * 阿里云翻译服务
 *
 * @author mabin
 * @date 2023/9/18 14:44
 */
public class AliYunTranslate extends AbstractTranslate {

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
        return null;
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
     * @date 2023/9/18 14:45
     */
    private String translate(String text, String source, String target) {

        return null;
    }
}

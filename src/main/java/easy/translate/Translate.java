package easy.translate;

import easy.config.translate.TranslateConfig;

/**
 * 翻译服务
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 17:31
 **/

public interface Translate {

    /**
     * 中译英
     *
     * @param zhStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 11:48
     **/
    String ch2En(String zhStr);

    /**
     * 英译中
     *
     * @param enStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 11:48
     **/
    String en2Ch(String enStr);

    /**
     * 初始化
     *
     * @param translateConfig
     * @return easy.service.Translate
     * @author mabin
     * @date 2023/9/4 11:50
     **/
    Translate init(TranslateConfig translateConfig);

    /**
     * 获取配置
     *
     * @param
     * @return easy.config.translate.TranslateConfig
     * @author mabin
     * @date 2023/9/4 11:50
     **/
    TranslateConfig getTranslateConfig();

    /**
     * 清除缓存
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/4 11:51
     **/
    void clearCache();

}

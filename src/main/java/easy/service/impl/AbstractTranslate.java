package easy.service.impl;

import easy.config.translate.TranslateConfig;
import easy.service.Translate;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 翻译抽象服务
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 17:33
 **/

public abstract class AbstractTranslate implements Translate {

    private final Map<String, String> en2ChCacheMap = new ConcurrentHashMap<>(16);
    private final Map<String, String> ch2EnCacheMap = new ConcurrentHashMap<>(16);
    private TranslateConfig translateConfig;

    /**
     * 中译英
     *
     * @param zhStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 11:48
     **/
    @Override
    public String ch2En(String zhStr) {
        if (StringUtils.isBlank(zhStr)) {
            return StringUtils.EMPTY;
        }
        String enStr = ch2EnCacheMap.get(zhStr);
        if (StringUtils.isNotBlank(enStr)) {
            return enStr;
        }
        enStr = translateCh2En(zhStr);
        if (StringUtils.isNotBlank(enStr)) {
            ch2EnCacheMap.put(zhStr, enStr);
        }
        return enStr;
    }

    /**
     * 英译中
     *
     * @param enStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 11:48
     **/
    @Override
    public String en2Ch(String enStr) {
        if (StringUtils.isBlank(enStr)) {
            return StringUtils.EMPTY;
        }
        String zhStr = en2ChCacheMap.get(enStr);
        if (StringUtils.isNotBlank(zhStr)) {
            return zhStr;
        }
        zhStr = translateEn2Ch(enStr);
        if (StringUtils.isNotBlank(zhStr)) {
            en2ChCacheMap.put(enStr, zhStr);
        }
        return zhStr;
    }

    /**
     * 初始化
     *
     * @param translateConfig
     * @return easy.service.Translate
     * @author mabin
     * @date 2023/9/4 11:50
     **/
    @Override
    public Translate init(TranslateConfig translateConfig) {
        this.translateConfig = translateConfig;
        return this;
    }

    /**
     * 获取配置
     *
     * @return easy.config.translate.TranslateConfig
     * @author mabin
     * @date 2023/9/4 11:50
     **/
    @Override
    public TranslateConfig getTranslateConfig() {
        return this.translateConfig;
    }

    /**
     * 清除缓存
     *
     * @return void
     * @author mabin
     * @date 2023/9/4 11:51
     **/
    @Override
    public void clearCache() {
        en2ChCacheMap.clear();
        ch2EnCacheMap.clear();
    }

    /**
     * 中译英
     *
     * @param chStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:36
     **/
    protected abstract String translateCh2En(String chStr);

    /**
     * 英译中
     *
     * @param enStr
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 13:36
     **/
    protected abstract String translateEn2Ch(String enStr);

}

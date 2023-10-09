package easy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.service.impl.*;
import easy.util.LanguageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 翻译服务实现
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 17:29
 **/

public class TranslateService {

    private static final Logger log = Logger.getInstance(TranslateConfig.class);

    private TranslateConfig translateConfig;

    private Map<String, Translate> translateMap;

    private static final Object LOCK = new Object();

    /**
     * 翻译引擎初始化
     *
     * @param translateConfig
     * @return void
     * @author mabin
     * @date 2023/9/4 20:49
     **/
    public void init(TranslateConfig translateConfig) {
        if (MapUtils.isNotEmpty(translateMap) && this.translateConfig != null) {
            return;
        }
        synchronized (LOCK) {
            if (MapUtils.isNotEmpty(translateMap) && this.translateConfig != null) {
                return;
            }
            translateMap = ImmutableMap.<String, Translate>builder()
                    .put(TranslateEnum.BAIDU.getTranslate(), new BaiDuTranslate().init(translateConfig))
                    .put(TranslateEnum.ALIYUN.getTranslate(), new AliYunTranslate().init(translateConfig))
                    .put(TranslateEnum.TENCENT.getTranslate(), new TencentTranslate().init(translateConfig))
                    .put(TranslateEnum.YOUDAO.getTranslate(), new YouDaoTranslate().init(translateConfig))
                    .put(TranslateEnum.VOLCANO.getTranslate(), new VolcanoTranslate().init(translateConfig))
                    .put(TranslateEnum.XFYUN.getTranslate(), new XfYunTranslate().init(translateConfig))
                    .put(TranslateEnum.GOOGLE.getTranslate(), new GoogleTranslate().init(translateConfig))
                    .put(TranslateEnum.MICROSOFT.getTranslate(), new MicrosoftTranslate().init(translateConfig))
                    .put(TranslateEnum.NIU.getTranslate(), new NiuTranslate().init(translateConfig))
                    .build();
            this.translateConfig = translateConfig;
        }
    }

    /**
     * 中英互译
     *
     * @param source
     * @return java.lang.String
     * @author mabin
     * @date 2023/9/4 20:59
     **/
    public String translate(String source) {
        if (StringUtils.isBlank(source)) {
            return StringUtils.EMPTY;
        }
        Translate translate = translateMap.get(translateConfig.getTranslateChannel());
        if (Objects.isNull(translate)) {
            return StringUtils.EMPTY;
        }
        if (LanguageUtil.isAllChinese(source)) {
            String enStr = translate.ch2En(source);
            List<String> chList = StringUtils.isBlank(enStr) ? Lists.newArrayList() : Lists.newArrayList(StringUtils.split(enStr));
            chList = chList.stream().filter(c -> !Constants.STOP_WORDS.contains(c.toLowerCase()))
                    .map(c -> c.replaceAll("[,.'\\-+;:`~]+", StringUtils.EMPTY))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(chList)) {
                return StringUtils.EMPTY;
            }
            int size = chList.size();
            if (size == 1) {
                return chList.get(0).toLowerCase();
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                String lowEn = chList.get(i);
                if (StringUtils.isBlank(lowEn)) {
                    continue;
                }
                if (Constants.STOP_WORDS.contains(lowEn.toLowerCase())) {
                    continue;
                }
                if (i == 0) {
                    builder.append(lowEn.toLowerCase());
                } else {
                    builder.append(StringUtils.substring(lowEn, 0, 1).toUpperCase())
                            .append(StringUtils.substring(lowEn, 1));
                }
            }
            return builder.toString();
        }
        return translate.en2Ch(source.replace(StringUtils.LF, StringUtils.SPACE));
    }

    /**
     * 清空缓存
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/4 21:19
     **/
    public void clearCache() {
        translateMap.values().forEach(Translate::clearCache);
    }


}

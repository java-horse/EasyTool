package easy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.enums.TranslateEnum;
import easy.service.impl.*;
import easy.util.LanguageUtil;
import easy.util.NotificationUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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

    private static final List<String> INVALID_CHARACTERS_LIST = Collections.unmodifiableList(Arrays.asList("/\\*\\*", "\\*", "\n", "\t", "\r"));

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
                    .put(TranslateEnum.CAIYUN.getTranslate(), new CaiYunTranslate().init(translateConfig))
                    .put(TranslateEnum.HUAWEI.getTranslate(), new HuaWeiTranslate().init(translateConfig))
                    .put(TranslateEnum.GOOGLE_FREE.getTranslate(), new GoogleFreeTranslate().init(translateConfig))
                    .put(TranslateEnum.KING_SOFT.getTranslate(), new KingSoftTranslate().init(translateConfig))
                    .put(TranslateEnum.MICROSOFT_FREE.getTranslate(), new MicrosoftFreeTranslate().init(translateConfig))
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
        String translateChannel = translateConfig.getTranslateChannel();
        if (Boolean.TRUE.equals(keyConfigurationReminder())) {
            translateChannel = TranslateEnum.KING_SOFT.getTranslate();
            NotificationUtil.notify("已自动切换免费翻译引擎，请及时配置当前翻译引擎【" + translateConfig.getTranslateChannel() + "】密钥", NotificationType.WARNING);
        }
        Translate translate = translateMap.get(translateChannel);
        if (StringUtils.isBlank(source) || Objects.isNull(translate)) {
            return StringUtils.EMPTY;
        }
        // 过滤无效字符
        for (String invalidChar : INVALID_CHARACTERS_LIST) {
            source = source.replaceAll(invalidChar, StringUtils.EMPTY);
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
        // 尝试分割分词, 翻译拼接处理
        if (LanguageUtil.isAllEnglish(source) || LanguageUtil.isCamelCase(source) || LanguageUtil.isSnakeCase(source)) {
            StringBuilder builder = new StringBuilder();
            String[] splitSources = new String[]{};
            if (LanguageUtil.isCamelCase(source)) {
                splitSources = LanguageUtil.splitCamelCase(source);
            } else if (LanguageUtil.isSnakeCase(source)) {
                splitSources = StringUtils.split(source, "_");
            } else if (StringUtils.contains(source, StringUtils.SPACE)) {
                splitSources = StringUtils.split(source, StringUtils.SPACE);
            }
            for (String split : splitSources) {
                String en2Ch = translate.en2Ch(split);
                if (StringUtils.isNotBlank(en2Ch)) {
                    builder.append(en2Ch);
                }
            }
            if (StringUtils.isBlank(builder.toString())) {
                builder.append(translate.en2Ch(source));
            }
            return builder.toString();
        }
        return translate.en2Ch(source);
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

    /**
     * 翻译渠道密钥配置提醒
     *
     * @param
     * @return java.lang.Boolean
     * @author mabin
     * @date 2023/9/17 16:35
     */
    public Boolean keyConfigurationReminder() {
        String translateChannel = translateConfig.getTranslateChannel();
        boolean isRemind = false;
        if (StringUtils.equals(translateChannel, TranslateEnum.BAIDU.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getAppId(), translateConfig.getAppSecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.ALIYUN.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getAccessKeyId(), translateConfig.getAccessKeySecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.YOUDAO.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getSecretId(), translateConfig.getSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.TENCENT.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getTencentSecretId(), translateConfig.getTencentSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.VOLCANO.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getVolcanoSecretId(), translateConfig.getVolcanoSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.XFYUN.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getXfAppId(), translateConfig.getXfApiKey(), translateConfig.getXfApiSecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.GOOGLE.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getGoogleSecretKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.MICROSOFT.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getMicrosoftKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.NIU.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getNiuApiKey());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.CAIYUN.getTranslate())) {
            isRemind = StringUtils.isBlank(translateConfig.getCaiyunToken());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.HUAWEI.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getHwProjectId(), translateConfig.getHwAppId(), translateConfig.getHwAppSecret());
        }
        return isRemind;
    }


}

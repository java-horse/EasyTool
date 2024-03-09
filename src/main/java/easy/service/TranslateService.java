package easy.service;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateEnum;
import easy.service.model.TongYiModelTranslate;
import easy.service.translate.*;
import easy.util.EasyCommonUtil;
import easy.util.LanguageUtil;
import easy.util.NotificationUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static final Pattern SPLIT_CAMEL_CASE_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("^\\W+");

    // 通知时间间隔 (3天之内打开项目只弹窗一次提示)
    private static final long INTERVAL = 3 * 24 * 60 * 60 * 1000L;

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
                    .put(TranslateEnum.THS_SOFT.getTranslate(), new THSTranslate().init(translateConfig))
                    .put(OpenModelTranslateEnum.TONG_YI.getModel(), new TongYiModelTranslate().init(translateConfig))
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
        if (StringUtils.equals(translateChannel, TranslateEnum.OPEN_BIG_MODEL.getTranslate())) {
            translateChannel = translateConfig.getOpenModelChannel();
        }
        String initTranslateChannel = translateChannel;
        if (Boolean.TRUE.equals(keyConfigurationReminder())) {
            translateChannel = TranslateEnum.KING_SOFT.getTranslate();
            // 每3天检测一次是否弹窗通知
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            long lastNoticeTime = propertiesComponent.getLong(Constants.Persistence.COMMON.TRANSLATE_CONFIG_LAST_NOTIFY_TIME, DateUtil.offsetDay(new Date(), -8).getTime());
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastNoticeTime > INTERVAL) {
                NotificationUtil.notify("已为您自动切换免费翻译引擎【" + translateChannel + "】，请及时配置当前翻译引擎【" + initTranslateChannel + "】密钥",
                        NotificationType.WARNING, EasyCommonUtil.getPluginSettingAction());
                propertiesComponent.setValue(Constants.Persistence.COMMON.TRANSLATE_CONFIG_LAST_NOTIFY_TIME, Long.toString(currentTimeMillis));
            }
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
            for (String lowEn : chList) {
                if (StringUtils.isBlank(lowEn) || Constants.STOP_WORDS.contains(lowEn.toLowerCase())) {
                    continue;
                }
                // 当第一个就是停用词时，首字母也需要大写
                if (builder.isEmpty()) {
                    builder.append(lowEn.toLowerCase());
                } else {
                    builder.append(StringUtils.substring(lowEn, 0, 1).toUpperCase())
                            .append(StringUtils.substring(lowEn, 1));
                }
            }
            return builder.toString();
        }
        // 英译中: 全量单词映射处理->尝试分割分词->再次单词映射处理->翻译拼接处理
        // 存在自定义单词: 单个单词翻译(不准确)
        // 不存在自定义单词: 整句翻译(更准确)
        SortedMap<String, String> wordMap = translateConfig.getGlobalWordMap();
        String originRes = ObjectUtils.firstNonNull(wordMap.get(source), wordMap.get(source.toLowerCase()), wordMap.get(source.toUpperCase()));
        if (StringUtils.isNotBlank(originRes)) {
            return originRes;
        }
        String analysisWords = analysisSource(source);
        List<String> allWordList = new ArrayList<>(Arrays.asList(StringUtils.split(analysisWords, StringUtils.SPACE)));
        if (CollectionUtils.containsAny(wordMap.keySet(), allWordList)) {
            StringBuilder customBuilder = new StringBuilder();
            for (String word : allWordList) {
                String res = ObjectUtils.firstNonNull(wordMap.get(word), wordMap.get(word.toLowerCase()), wordMap.get(word.toUpperCase()));
                if (StringUtils.isBlank(res)) {
                    res = translate.en2Ch(word);
                }
                customBuilder.append(res);
            }
            return customBuilder.toString();
        } else {
            return translate.en2Ch(analysisWords);
        }
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
        } else if (StringUtils.equals(translateChannel, TranslateEnum.THS_SOFT.getTranslate())) {
            isRemind = StringUtils.isAnyBlank(translateConfig.getThsAppId(), translateConfig.getThsAppSecret());
        } else if (StringUtils.equals(translateChannel, TranslateEnum.OPEN_BIG_MODEL.getTranslate())) {
            String openModelChannel = translateConfig.getOpenModelChannel();
            if (StringUtils.equals(openModelChannel, OpenModelTranslateEnum.TONG_YI.getModel())) {
                isRemind = StringUtils.isBlank(translateConfig.getTyKey());
            }
        }
        return isRemind;
    }

    /**
     * 分析英文单词或其他信息
     *
     * @param text
     * @return java.lang.String
     * @author mabin
     * @date 2024/1/11 14:31
     */
    private String analysisSource(String text) {
        String resultText;
        String appendText = StringUtils.EMPTY;
        Matcher matcher = SPECIAL_CHAR_PATTERN.matcher(text);
        if (matcher.find()) {
            appendText = matcher.group(0);
        }
        text = text.replaceAll("^\\W+", StringUtils.EMPTY);
        boolean isLower = StringUtils.equals(text, text.toLowerCase());
        boolean isUpper = StringUtils.equals(text, text.toUpperCase());
        if (isLower && text.contains("_")) {
            // snake_case
            resultText = text.replaceAll("_", StringUtils.SPACE);
        } else if (isLower && text.contains(StringUtils.SPACE)) {
            // snake case
            resultText = text;
        } else if (StringUtils.isNotBlank(text) && text.length() >= 2
                && Character.isUpperCase(text.charAt(0)) && Character.isLowerCase(text.charAt(1)) && text.contains(StringUtils.SPACE)) {
            // SNAKE CASE
            resultText = text.toLowerCase();
        } else if (isLower && text.contains("-") || (isLower && !text.contains(StringUtils.SPACE))) {
            // snake-case
            resultText = text.replaceAll("-", StringUtils.SPACE);
        } else if ((isUpper && text.contains("_")) || (isLower && !text.contains("_") && !text.contains(StringUtils.SPACE))
                || (isUpper && !text.contains(StringUtils.SPACE))) {
            // SNAKE_CASE
            resultText = text.replaceAll("_", StringUtils.SPACE).toLowerCase();
        } else if (!isUpper && text.substring(0, 1).equals(text.substring(0, 1).toUpperCase()) && !text.contains("_")) {
            // SnakeCase
            String caseText = text.substring(0, 1).toLowerCase() + text.substring(1);
            resultText = SPLIT_CAMEL_CASE_PATTERN.matcher(caseText).replaceAll(StringUtils.SPACE).toLowerCase();
        } else {
            // snakeCase
            resultText = SPLIT_CAMEL_CASE_PATTERN.matcher(text).replaceAll(StringUtils.SPACE).toLowerCase();
        }
        return appendText + resultText.replaceAll("-", StringUtils.SPACE)
                .replaceAll("_", StringUtils.SPACE)
                .replaceAll("\\.", StringUtils.SPACE);
    }

}

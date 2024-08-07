package easy.config.translate;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import easy.base.Constants;
import easy.base.ModelConstants;
import easy.enums.*;
import org.apache.commons.lang3.StringUtils;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 翻译渠道设置
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 15:32
 **/

public class TranslateConfig {

    /**
     * 翻译渠道
     */
    private String translateChannel;

    /**
     * 百度appId
     */
    private String appId;

    /**
     * 百度appSecret
     */
    private String appSecret;

    /**
     * 是否开启百度翻译领域模型
     */
    private Boolean baiduDomainCheckBox;

    /**
     * 百度翻译领域模型名称
     */
    private String baiduDomainComboBox;

    /**
     * 有道secretId
     */
    private String secretId;

    /**
     * 有道secretKey
     */
    private String secretKey;

    /**
     * 是否开启有道领域翻译
     */
    private Boolean youdaoDomainCheckBox;
    /**
     * 有道领域翻译
     */
    private String youdaoDomainComboBox;

    /**
     * 阿里accessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里accessKeySecret
     */
    private String accessKeySecret;

    /**
     * 是否开启阿里翻译专业版领域模型
     */
    private Boolean aliyunDomainCheckBox;
    /**
     * 专业版领域模型
     */
    private String aliyunDomainComboBox;

    /**
     * 腾讯secretId
     */
    private String tencentSecretId;

    /**
     * 腾讯secretKey
     */
    private String tencentSecretKey;

    /**
     * 火山SecretId
     */
    private String volcanoSecretId;

    /**
     * 火山SecretKey
     */
    private String volcanoSecretKey;

    /**
     * 讯飞AppId
     */
    private String xfAppId;

    /**
     * 讯飞AppSecret
     */
    private String xfApiSecret;

    /**
     * 讯飞AppKey
     */
    private String xfApiKey;

    /**
     * 谷歌key
     */
    private String googleSecretKey;

    /**
     * 微软key
     */
    private String microsoftKey;

    /**
     * 小牛翻译apiKey
     */
    private String niuApiKey;

    /**
     * 彩云翻译token
     */
    private String caiyunToken;

    /**
     * 华为翻译项目ID
     */
    private String hwProjectId;

    /**
     * 华为翻译应用ID
     */
    private String hwAppId;

    /**
     * 华为翻译应用密钥
     */
    private String hwAppSecret;

    /**
     * 同花顺appId
     */
    private String thsAppId;

    /**
     * 同花顺appSecret
     */
    private String thsAppSecret;

    /**
     * 开源模型渠道
     */
    private String openModelChannel;

    /**
     * 通义千问model
     */
    private String tyModel;

    /**
     * 通义千问key
     */
    private String tyKey;

    /**
     * kimi大模型model
     */
    private String kimiModel;

    /**
     * kimi大模型密钥
     */
    private String kimiKey;

    /**
     * 文心一言模型model
     */
    private String wenxinModel;

    /**
     * 文心一言模型ApiKey
     */
    private String wenxinApiKey;

    /**
     * 文心一言模型ApiSecret
     */
    private String wenxinApiSecret;

    /**
     * 自定义API url
     */
    private String customApiUrl;

    /**
     * 自定义API最大字符长度, 默认:1000
     */
    private Integer customApiMaxCharLength;

    /**
     * 自定义支持语言文本字段
     */
    private String customSupportLanguage;

    /**
     * libreTranslate服务地址
     */
    private String libreServerUrl;

    /**
     * 全局单词映射
     */
    private SortedMap<String, String> globalWordMap = new TreeMap<>();

    /**
     * 开启实时备份
     */
    private Boolean backupSwitch;
    /**
     * 备份文件路径
     */
    private String backupFilePath;


    /**
     * 重置翻译配置
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/3 15:36
     **/
    public void reset() {
        translateChannel = TranslateEnum.BAIDU.getTranslate();
        globalWordMap = new TreeMap<>();
        appId = null;
        appSecret = null;
        baiduDomainCheckBox = Boolean.FALSE;
        baiduDomainComboBox = BaiDuTranslateDomainEnum.IT.getName();
        secretId = null;
        secretKey = null;
        accessKeyId = null;
        accessKeySecret = null;
        tencentSecretId = null;
        tencentSecretKey = null;
        volcanoSecretId = null;
        volcanoSecretKey = null;
        xfAppId = null;
        xfApiKey = null;
        xfApiSecret = null;
        googleSecretKey = null;
        microsoftKey = null;
        niuApiKey = null;
        caiyunToken = null;
        hwProjectId = null;
        hwAppId = null;
        hwAppSecret = null;
        openModelChannel = OpenModelTranslateEnum.TONG_YI.getModel();
        tyModel = ModelConstants.TONG_YI.TURBO.getModel();
        tyKey = null;
        aliyunDomainCheckBox = Boolean.FALSE;
        aliyunDomainComboBox = AliYunTranslateDomainEnum.SOCIAL.getName();
        youdaoDomainCheckBox = Boolean.FALSE;
        youdaoDomainComboBox = YouDaoTranslateDomainEnum.COMPUTERS.getName();
        kimiModel = null;
        kimiKey = null;
        wenxinModel = null;
        wenxinApiKey = null;
        wenxinApiSecret = null;
        customApiUrl = null;
        customApiMaxCharLength = Constants.NUM.ONE_THOUSAND;
        customSupportLanguage = TranslateLanguageEnum.EN.lang + StrUtil.COMMA + TranslateLanguageEnum.ZH_CN.lang;
        libreServerUrl = null;
        backupSwitch = Boolean.FALSE;
        backupFilePath = StringUtils.EMPTY;
    }

    public SortedMap<String, String> getGlobalWordMap() {
        if (globalWordMap == null) {
            globalWordMap = Maps.newTreeMap();
        }
        return globalWordMap;
    }

    public void setGlobalWordMap(SortedMap<String, String> globalWordMap) {
        this.globalWordMap = globalWordMap;
    }

    public String getTranslateChannel() {
        return translateChannel;
    }

    public void setTranslateChannel(String translateChannel) {
        this.translateChannel = translateChannel;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Boolean getBaiduDomainCheckBox() {
        return baiduDomainCheckBox;
    }

    public void setBaiduDomainCheckBox(Boolean baiduDomainCheckBox) {
        this.baiduDomainCheckBox = baiduDomainCheckBox;
    }

    public String getBaiduDomainComboBox() {
        return baiduDomainComboBox;
    }

    public void setBaiduDomainComboBox(String baiduDomainComboBox) {
        this.baiduDomainComboBox = baiduDomainComboBox;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getTencentSecretId() {
        return tencentSecretId;
    }

    public void setTencentSecretId(String tencentSecretId) {
        this.tencentSecretId = tencentSecretId;
    }

    public String getTencentSecretKey() {
        return tencentSecretKey;
    }

    public void setTencentSecretKey(String tencentSecretKey) {
        this.tencentSecretKey = tencentSecretKey;
    }

    public String getVolcanoSecretId() {
        return volcanoSecretId;
    }

    public void setVolcanoSecretId(String volcanoSecretId) {
        this.volcanoSecretId = volcanoSecretId;
    }

    public String getVolcanoSecretKey() {
        return volcanoSecretKey;
    }

    public void setVolcanoSecretKey(String volcanoSecretKey) {
        this.volcanoSecretKey = volcanoSecretKey;
    }

    public String getXfAppId() {
        return xfAppId;
    }

    public void setXfAppId(String xfAppId) {
        this.xfAppId = xfAppId;
    }

    public String getXfApiSecret() {
        return xfApiSecret;
    }

    public void setXfApiSecret(String xfApiSecret) {
        this.xfApiSecret = xfApiSecret;
    }

    public String getXfApiKey() {
        return xfApiKey;
    }

    public void setXfApiKey(String xfApiKey) {
        this.xfApiKey = xfApiKey;
    }

    public String getGoogleSecretKey() {
        return googleSecretKey;
    }

    public void setGoogleSecretKey(String googleSecretKey) {
        this.googleSecretKey = googleSecretKey;
    }

    public String getMicrosoftKey() {
        return microsoftKey;
    }

    public void setMicrosoftKey(String microsoftKey) {
        this.microsoftKey = microsoftKey;
    }

    public String getNiuApiKey() {
        return niuApiKey;
    }

    public void setNiuApiKey(String niuApiKey) {
        this.niuApiKey = niuApiKey;
    }

    public String getCaiyunToken() {
        return caiyunToken;
    }

    public void setCaiyunToken(String caiyunToken) {
        this.caiyunToken = caiyunToken;
    }

    public String getHwProjectId() {
        return hwProjectId;
    }

    public void setHwProjectId(String hwProjectId) {
        this.hwProjectId = hwProjectId;
    }

    public String getHwAppId() {
        return hwAppId;
    }

    public void setHwAppId(String hwAppId) {
        this.hwAppId = hwAppId;
    }

    public String getHwAppSecret() {
        return hwAppSecret;
    }

    public void setHwAppSecret(String hwAppSecret) {
        this.hwAppSecret = hwAppSecret;
    }

    public String getThsAppId() {
        return thsAppId;
    }

    public void setThsAppId(String thsAppId) {
        this.thsAppId = thsAppId;
    }

    public String getThsAppSecret() {
        return thsAppSecret;
    }

    public void setThsAppSecret(String thsAppSecret) {
        this.thsAppSecret = thsAppSecret;
    }

    public String getOpenModelChannel() {
        return openModelChannel;
    }

    public void setOpenModelChannel(String openModelChannel) {
        this.openModelChannel = openModelChannel;
    }

    public String getTyKey() {
        return tyKey;
    }

    public void setTyKey(String tyKey) {
        this.tyKey = tyKey;
    }

    public String getTyModel() {
        return tyModel;
    }

    public void setTyModel(String tyModel) {
        this.tyModel = tyModel;
    }

    public Boolean getAliyunDomainCheckBox() {
        return aliyunDomainCheckBox;
    }

    public void setAliyunDomainCheckBox(Boolean aliyunDomainCheckBox) {
        this.aliyunDomainCheckBox = aliyunDomainCheckBox;
    }

    public String getAliyunDomainComboBox() {
        return aliyunDomainComboBox;
    }

    public void setAliyunDomainComboBox(String aliyunDomainComboBox) {
        this.aliyunDomainComboBox = aliyunDomainComboBox;
    }

    public Boolean getYoudaoDomainCheckBox() {
        return youdaoDomainCheckBox;
    }

    public void setYoudaoDomainCheckBox(Boolean youdaoDomainCheckBox) {
        this.youdaoDomainCheckBox = youdaoDomainCheckBox;
    }

    public String getYoudaoDomainComboBox() {
        return youdaoDomainComboBox;
    }

    public void setYoudaoDomainComboBox(String youdaoDomainComboBox) {
        this.youdaoDomainComboBox = youdaoDomainComboBox;
    }

    public String getKimiModel() {
        return kimiModel;
    }

    public void setKimiModel(String kimiModel) {
        this.kimiModel = kimiModel;
    }

    public String getKimiKey() {
        return kimiKey;
    }

    public void setKimiKey(String kimiKey) {
        this.kimiKey = kimiKey;
    }

    public String getWenxinModel() {
        return wenxinModel;
    }

    public void setWenxinModel(String wenxinModel) {
        this.wenxinModel = wenxinModel;
    }

    public String getWenxinApiKey() {
        return wenxinApiKey;
    }

    public void setWenxinApiKey(String wenxinApiKey) {
        this.wenxinApiKey = wenxinApiKey;
    }

    public String getWenxinApiSecret() {
        return wenxinApiSecret;
    }

    public void setWenxinApiSecret(String wenxinApiSecret) {
        this.wenxinApiSecret = wenxinApiSecret;
    }

    public String getCustomApiUrl() {
        return customApiUrl;
    }

    public void setCustomApiUrl(String customApiUrl) {
        this.customApiUrl = customApiUrl;
    }

    public Integer getCustomApiMaxCharLength() {
        return customApiMaxCharLength;
    }

    public void setCustomApiMaxCharLength(Integer customApiMaxCharLength) {
        this.customApiMaxCharLength = customApiMaxCharLength;
    }

    public String getCustomSupportLanguage() {
        return customSupportLanguage;
    }

    public void setCustomSupportLanguage(String customSupportLanguage) {
        this.customSupportLanguage = customSupportLanguage;
    }

    public String getLibreServerUrl() {
        return libreServerUrl;
    }

    public void setLibreServerUrl(String libreServerUrl) {
        this.libreServerUrl = libreServerUrl;
    }

    public Boolean getBackupSwitch() {
        return backupSwitch;
    }

    public void setBackupSwitch(Boolean backupSwitch) {
        this.backupSwitch = backupSwitch;
    }

    public String getBackupFilePath() {
        return backupFilePath;
    }

    public void setBackupFilePath(String backupFilePath) {
        this.backupFilePath = backupFilePath;
    }

}

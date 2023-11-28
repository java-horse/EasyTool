package easy.config.translate;

import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateEnum;

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
     * 有道secretId
     */
    private String secretId;

    /**
     * 有道secretKey
     */
    private String secretKey;

    /**
     * 阿里accessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里accessKeySecret
     */
    private String accessKeySecret;

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
     * 通义千问key
     */
    private String tyKey;

    /**
     * 文心一言key
     */
    private String wxKey;


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
        appId = null;
        appSecret = null;
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
        tyKey = null;
        wxKey = null;
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

    public String getWxKey() {
        return wxKey;
    }

    public void setWxKey(String wxKey) {
        this.wxKey = wxKey;
    }

    @Override
    public String toString() {
        return "TranslateConfig{" +
                "translateChannel='" + translateChannel + '\'' +
                ", appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", secretId='" + secretId + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", tencentSecretId='" + tencentSecretId + '\'' +
                ", tencentSecretKey='" + tencentSecretKey + '\'' +
                ", volcanoSecretId='" + volcanoSecretId + '\'' +
                ", volcanoSecretKey='" + volcanoSecretKey + '\'' +
                ", xfAppId='" + xfAppId + '\'' +
                ", xfApiSecret='" + xfApiSecret + '\'' +
                ", xfApiKey='" + xfApiKey + '\'' +
                ", googleSecretKey='" + googleSecretKey + '\'' +
                ", microsoftKey='" + microsoftKey + '\'' +
                ", niuApiKey='" + niuApiKey + '\'' +
                ", caiyunToken='" + caiyunToken + '\'' +
                ", hwProjectId='" + hwProjectId + '\'' +
                ", hwAppId='" + hwAppId + '\'' +
                ", hwAppSecret='" + hwAppSecret + '\'' +
                ", thsAppId='" + thsAppId + '\'' +
                ", thsAppSecret='" + thsAppSecret + '\'' +
                ", openModelChannel='" + openModelChannel + '\'' +
                ", tyKey='" + tyKey + '\'' +
                ", wxKey='" + wxKey + '\'' +
                '}';
    }

}

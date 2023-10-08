package easy.config.translate;

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
     * 谷歌Key
     */
    private String googleSecretKey;


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
                '}';
    }

}

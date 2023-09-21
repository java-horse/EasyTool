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
                '}';
    }

}

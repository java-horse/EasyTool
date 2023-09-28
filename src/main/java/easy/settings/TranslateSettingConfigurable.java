package easy.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.TranslateEnum;
import easy.form.TranslateSettingView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 翻译渠道设置
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 16:02
 **/

public class TranslateSettingConfigurable implements Configurable {

    private TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
    private TranslateSettingView translateSettingView = new TranslateSettingView();


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Translate";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return translateSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(translateConfig.getTranslateChannel(), translateSettingView.getTranslateChannelBox().getSelectedItem())
                || !Objects.equals(translateConfig.getAppId(), translateSettingView.getAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getAppSecret(), translateSettingView.getAppSecretTextField().getText())
                || !StringUtils.equals(translateConfig.getSecretId(), translateSettingView.getSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getSecretKey(), translateSettingView.getSecretKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getAccessKeyId(), translateSettingView.getAccessKeyIdTextField().getText())
                || !StringUtils.equals(translateConfig.getAccessKeySecret(), translateSettingView.getAccessKeySecretTextField().getText())
                || !StringUtils.equals(translateConfig.getTencentSecretId(), translateSettingView.getTencentSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getTencentSecretKey(), translateSettingView.getTencentSecretKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getVolcanoSecretId(), translateSettingView.getVolcanoSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getVolcanoSecretKey(), translateSettingView.getVolcanoSecretKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getXfAppId(), translateSettingView.getXfAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getXfApiKey(), translateSettingView.getXfApiKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getXfApiSecret(), translateSettingView.getXfApiSecretTextField().getText());
    }

    @Override
    public void reset() {
        translateSettingView.refresh();
    }

    @Override
    public void apply() throws ConfigurationException {
        translateConfig.setTranslateChannel(String.valueOf(translateSettingView.getTranslateChannelBox().getSelectedItem()));
        translateConfig.setAppId(translateSettingView.getAppIdTextField().getText());
        translateConfig.setAppSecret(translateSettingView.getAppSecretTextField().getText());
        translateConfig.setSecretId(translateSettingView.getSecretIdTextField().getText());
        translateConfig.setSecretKey(translateSettingView.getSecretKeyTextField().getText());
        translateConfig.setAccessKeyId(translateSettingView.getAccessKeyIdTextField().getText());
        translateConfig.setAccessKeySecret(translateSettingView.getAccessKeySecretTextField().getText());
        translateConfig.setTencentSecretId(translateSettingView.getTencentSecretIdTextField().getText());
        translateConfig.setTencentSecretKey(translateSettingView.getTencentSecretKeyTextField().getText());
        translateConfig.setVolcanoSecretId(translateSettingView.getVolcanoSecretIdTextField().getText());
        translateConfig.setVolcanoSecretKey(translateSettingView.getVolcanoSecretKeyTextField().getText());
        translateConfig.setXfAppId(translateSettingView.getXfAppIdTextField().getText());
        translateConfig.setXfApiKey(translateSettingView.getXfApiKeyTextField().getText());
        translateConfig.setXfApiSecret(translateSettingView.getXfApiSecretTextField().getText());
        if (Objects.isNull(translateConfig.getTranslateChannel()) || !TranslateEnum.getTranslator().contains(translateConfig.getTranslateChannel())) {
            throw new ConfigurationException("请选择正确的翻译渠道");
        }
        if (TranslateEnum.BAIDU.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getAppId())) {
                throw new ConfigurationException("百度AppId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getAppSecret())) {
                throw new ConfigurationException("百度AppSecret不能为空");
            }
        } else if (TranslateEnum.ALIYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getAccessKeyId())) {
                throw new ConfigurationException("阿里云AccessKeyId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getAccessKeySecret())) {
                throw new ConfigurationException("阿里云AccessKeySecret不能为空");
            }
        } else if (TranslateEnum.YOUDAO.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getSecretId())) {
                throw new ConfigurationException("有道智云SecretId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getSecretKey())) {
                throw new ConfigurationException("有道智云SecretKey不能为空");
            }
        } else if (TranslateEnum.TENCENT.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getTencentSecretId())) {
                throw new ConfigurationException("腾讯云TencentSecretId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getTencentSecretKey())) {
                throw new ConfigurationException("腾讯云TencentSecretKey不能为空");
            }
        } else if (TranslateEnum.VOLCANO.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getVolcanoSecretId())) {
                throw new ConfigurationException("火山云VolcanoSecretId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getVolcanoSecretKey())) {
                throw new ConfigurationException("火山云VolcanoSecretKey不能为空");
            }
        } else if (TranslateEnum.XFYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getXfAppId())) {
                throw new ConfigurationException("讯飞云appId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getXfApiKey())) {
                throw new ConfigurationException("讯飞云apiKey不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getXfApiSecret())) {
                throw new ConfigurationException("讯飞云apiSecret不能为空");
            }
        }
    }

}

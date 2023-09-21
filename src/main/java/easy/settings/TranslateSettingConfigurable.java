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
        if (!Objects.equals(translateConfig.getTranslateChannel(), translateSettingView.getTranslateChannelBox().getSelectedItem())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getAppId(), translateSettingView.getAppIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getAppSecret(), translateSettingView.getAppSecretTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getSecretId(), translateSettingView.getSecretIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getSecretKey(), translateSettingView.getSecretKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getAccessKeyId(), translateSettingView.getAccessKeyIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getAccessKeySecret(), translateSettingView.getAccessKeySecretTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getTencentSecretId(), translateSettingView.getTencentSecretIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(translateConfig.getTencentSecretKey(), translateSettingView.getTencentSecretKeyTextField().getText())) {
            return true;
        }
        return false;
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
                throw new ConfigurationException("有道SecretId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getSecretKey())) {
                throw new ConfigurationException("有道SecretKey不能为空");
            }
        } else if (TranslateEnum.TENCENT.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (StringUtils.isBlank(translateConfig.getTencentSecretId())) {
                throw new ConfigurationException("腾讯SecretId不能为空");
            }
            if (StringUtils.isBlank(translateConfig.getTencentSecretKey())) {
                throw new ConfigurationException("腾讯SecretKey不能为空");
            }
        }
    }

}

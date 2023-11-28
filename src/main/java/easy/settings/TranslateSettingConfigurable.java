package easy.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateEnum;
import easy.form.TranslateSettingView;
import easy.util.ValidatorUtil;
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
                || !StringUtils.equals(translateConfig.getAppId(), translateSettingView.getAppIdTextField().getText())
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
                || !StringUtils.equals(translateConfig.getXfApiSecret(), translateSettingView.getXfApiSecretTextField().getText())
                || !StringUtils.equals(translateConfig.getGoogleSecretKey(), translateSettingView.getGoogleSecretKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getMicrosoftKey(), translateSettingView.getMicrosoftKeyLabel().getText())
                || !StringUtils.equals(translateConfig.getNiuApiKey(), translateSettingView.getNiuApiKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getCaiyunToken(), translateSettingView.getCaiyunTokenTextField().getText())
                || !StringUtils.equals(translateConfig.getHwAppId(), translateSettingView.getHwAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getHwAppSecret(), translateSettingView.getAppSecretTextField().getText())
                || !StringUtils.equals(translateConfig.getHwProjectId(), translateSettingView.getHwProjectIdTextField().getText())
                || !StringUtils.equals(translateConfig.getThsAppId(), translateSettingView.getThsAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getThsAppSecret(), translateSettingView.getThsAppSecretTextField().getText())
                || !Objects.equals(translateConfig.getOpenModelChannel(), translateSettingView.getOpenModelComboBox().getSelectedItem())
                || !Objects.equals(translateConfig.getTyModel(), translateSettingView.getTyModelComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getTyKey(), translateSettingView.getTyKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getWxKey(), translateSettingView.getWxKeyTextField().getText());
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
        translateConfig.setGoogleSecretKey(translateSettingView.getGoogleSecretKeyTextField().getText());
        translateConfig.setMicrosoftKey(translateSettingView.getMicrosoftKeyTextField().getText());
        translateConfig.setNiuApiKey(translateSettingView.getNiuApiKeyTextField().getText());
        translateConfig.setCaiyunToken(translateSettingView.getCaiyunTokenTextField().getText());
        translateConfig.setHwProjectId(translateSettingView.getHwProjectIdTextField().getText());
        translateConfig.setHwAppId(translateSettingView.getHwAppIdTextField().getText());
        translateConfig.setHwAppSecret(translateSettingView.getHwAppSecretTextField().getText());
        translateConfig.setThsAppId(translateSettingView.getThsAppIdTextField().getText());
        translateConfig.setThsAppSecret(translateSettingView.getThsAppSecretTextField().getText());
        translateConfig.setOpenModelChannel(String.valueOf(translateSettingView.getOpenModelComboBox().getSelectedItem()));
        translateConfig.setTyModel(String.valueOf(translateSettingView.getTyModelComboBox().getSelectedItem()));
        translateConfig.setTyKey(translateSettingView.getTyKeyTextField().getText());
        translateConfig.setWxKey(translateSettingView.getWxKeyTextField().getText());

        ValidatorUtil.notTrue(StringUtils.isBlank(translateConfig.getTranslateChannel()) || !TranslateEnum.getTranslator().contains(translateConfig.getTranslateChannel()), "请选择正确的翻译渠道");
        if (TranslateEnum.BAIDU.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getAppId(), translateConfig.getAppSecret()), TranslateEnum.BAIDU.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.ALIYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getAccessKeyId(), translateConfig.getAccessKeySecret()), TranslateEnum.ALIYUN.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.YOUDAO.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getSecretId(), translateConfig.getSecretKey()), TranslateEnum.YOUDAO.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.TENCENT.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getTencentSecretId(), translateConfig.getTencentSecretKey()), TranslateEnum.TENCENT.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.VOLCANO.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getVolcanoSecretId(), translateConfig.getVolcanoSecretKey()), TranslateEnum.VOLCANO.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.XFYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getXfAppId(), translateConfig.getXfApiKey(), translateConfig.getXfApiSecret()), TranslateEnum.XFYUN.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.GOOGLE.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNotBlank(translateConfig.getGoogleSecretKey()), TranslateEnum.GOOGLE.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.MICROSOFT.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNotBlank(translateConfig.getMicrosoftKey()), TranslateEnum.MICROSOFT.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.NIU.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNotBlank(translateConfig.getNiuApiKey()), TranslateEnum.NIU.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.CAIYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNotBlank(translateConfig.getCaiyunToken()), TranslateEnum.CAIYUN.getTranslate() + "Token不能为空");
        }
        if (TranslateEnum.HUAWEI.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getHwProjectId(), translateConfig.getHwAppId(), translateConfig.getHwAppSecret()), TranslateEnum.HUAWEI.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.THS_SOFT.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getThsAppId(), translateConfig.getThsAppSecret()), TranslateEnum.THS_SOFT.getTranslate() + "密钥不能为空");
        }
        if (TranslateEnum.OPEN_BIG_MODEL.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (OpenModelTranslateEnum.TONG_YI.getModel().equals(translateConfig.getOpenModelChannel())) {
                ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getTyKey(), translateConfig.getTyModel()), OpenModelTranslateEnum.TONG_YI.getModel() + "配置不能为空");
            } else if (OpenModelTranslateEnum.WEN_XIN.getModel().equals(translateConfig.getOpenModelChannel())) {
                ValidatorUtil.isTrue(StringUtils.isNotBlank(translateConfig.getWxKey()), OpenModelTranslateEnum.WEN_XIN.getModel() + "密钥不能为空");
            }
        }
    }

}

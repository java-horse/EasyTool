package easy.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateEnum;
import easy.form.TranslateSettingView;
import easy.helper.ServiceHelper;
import easy.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 翻译渠道设置
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 16:02
 **/

public class TranslateSettingConfigurable implements Configurable {

    private TranslateConfig translateConfig = ServiceHelper.getService(TranslateConfigComponent.class).getState();
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
                || !StringUtils.equals(translateConfig.getAppSecret(), String.valueOf(translateSettingView.getAppSecretTextField().getPassword()))
                || !Objects.equals(translateConfig.getBaiduDomainCheckBox(), translateSettingView.getBaiduDomainCheckBox().isSelected())
                || !Objects.equals(translateConfig.getBaiduDomainComboBox(), translateSettingView.getBaiduDomainComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getSecretId(), translateSettingView.getSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getSecretKey(), String.valueOf(translateSettingView.getSecretKeyTextField().getPassword()))
                || !Objects.equals(translateConfig.getYoudaoDomainCheckBox(), translateSettingView.getYoudaoDomainCheckBox().isSelected())
                || !Objects.equals(translateConfig.getYoudaoDomainComboBox(), translateSettingView.getYoudaoDomainComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getAccessKeyId(), translateSettingView.getAccessKeyIdTextField().getText())
                || !StringUtils.equals(translateConfig.getAccessKeySecret(), String.valueOf(translateSettingView.getAccessKeySecretTextField().getPassword()))
                || !Objects.equals(translateConfig.getAliyunDomainCheckBox(), translateSettingView.getAliyunDomainCheckBox().isSelected())
                || !Objects.equals(translateConfig.getAliyunDomainComboBox(), translateSettingView.getAliyunDomainComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getTencentSecretId(), translateSettingView.getTencentSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getTencentSecretKey(),
                String.valueOf(translateSettingView.getTencentSecretKeyTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getVolcanoSecretId(), translateSettingView.getVolcanoSecretIdTextField().getText())
                || !StringUtils.equals(translateConfig.getVolcanoSecretKey(),
                String.valueOf(translateSettingView.getVolcanoSecretKeyTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getXfAppId(), translateSettingView.getXfAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getXfApiKey(), translateSettingView.getXfApiKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getXfApiSecret(), String.valueOf(translateSettingView.getXfApiSecretTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getGoogleSecretKey(), String.valueOf(translateSettingView.getGoogleSecretKeyTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getMicrosoftKey(), translateSettingView.getMicrosoftKeyLabel().getText())
                || !StringUtils.equals(translateConfig.getNiuApiKey(), String.valueOf(translateSettingView.getNiuApiKeyTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getCaiyunToken(), String.valueOf(translateSettingView.getCaiyunTokenTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getHwAppId(), translateSettingView.getHwAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getHwAppSecret(), String.valueOf(translateSettingView.getAppSecretTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getHwProjectId(), translateSettingView.getHwProjectIdTextField().getText())
                || !StringUtils.equals(translateConfig.getThsAppId(), translateSettingView.getThsAppIdTextField().getText())
                || !StringUtils.equals(translateConfig.getThsAppSecret(), String.valueOf(translateSettingView.getThsAppSecretTextField().getPassword()))
                || !StringUtils.equals(translateConfig.getCustomApiUrl(), translateSettingView.getCustomApiUrlTextField().getText())
                || !StringUtils.equals(translateConfig.getCustomSupportLanguage(), translateSettingView.getCustomSupportLanguageTextField().getText())
                || !Objects.equals(translateConfig.getCustomApiMaxCharLength(), Integer.parseInt(translateSettingView.getCustomApiMaxCharLengthTextField().getText()))
                || !Objects.equals(translateConfig.getOpenModelChannel(), translateSettingView.getOpenModelComboBox().getSelectedItem())
                || !Objects.equals(translateConfig.getTyModel(), translateSettingView.getTyModelComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getTyKey(), String.valueOf(translateSettingView.getTyKeyTextField().getPassword()))
                || !Objects.equals(translateConfig.getKimiModel(), translateSettingView.getKimiModelComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getKimiKey(), String.valueOf(translateSettingView.getKimiKeyPasswordField().getPassword()))
                || !Objects.equals(translateConfig.getWenxinModel(), translateSettingView.getWenxinModelComboBox().getSelectedItem())
                || !StringUtils.equals(translateConfig.getWenxinApiKey(), translateSettingView.getWenxinApiKeyTextField().getText())
                || !StringUtils.equals(translateConfig.getWenxinApiSecret(), String.valueOf(translateSettingView.getWenxinApiSecretPasswordField().getPassword()))
                || !StringUtils.equals(translateConfig.getLibreServerUrl(), String.valueOf(translateSettingView.getLibreServerUrlComboBox().getSelectedItem()));
    }

    @Override
    public void reset() {
        translateSettingView.refresh();
    }

    @Override
    public void apply() throws ConfigurationException {
        // 配置赋值持久化
        if (translateConfig.getGlobalWordMap() == null) {
            translateConfig.setGlobalWordMap(new TreeMap<>());
        }
        translateConfig.setTranslateChannel(String.valueOf(translateSettingView.getTranslateChannelBox().getSelectedItem()));
        translateConfig.setAppId(translateSettingView.getAppIdTextField().getText());
        translateConfig.setAppSecret(String.valueOf(translateSettingView.getAppSecretTextField().getPassword()));
        translateConfig.setBaiduDomainCheckBox(translateSettingView.getBaiduDomainCheckBox().isSelected());
        translateConfig.setBaiduDomainComboBox(String.valueOf(translateSettingView.getBaiduDomainComboBox().getSelectedItem()));
        translateConfig.setSecretId(translateSettingView.getSecretIdTextField().getText());
        translateConfig.setSecretKey(String.valueOf(translateSettingView.getSecretKeyTextField().getPassword()));
        translateConfig.setYoudaoDomainCheckBox(translateSettingView.getYoudaoDomainCheckBox().isSelected());
        translateConfig.setYoudaoDomainComboBox(String.valueOf(translateSettingView.getYoudaoDomainComboBox().getSelectedItem()));
        translateConfig.setAccessKeyId(translateSettingView.getAccessKeyIdTextField().getText());
        translateConfig.setAccessKeySecret(String.valueOf(translateSettingView.getAccessKeySecretTextField().getPassword()));
        translateConfig.setAliyunDomainCheckBox(translateSettingView.getAliyunDomainCheckBox().isSelected());
        translateConfig.setAliyunDomainComboBox(String.valueOf(translateSettingView.getAliyunDomainComboBox().getSelectedItem()));
        translateConfig.setTencentSecretId(translateSettingView.getTencentSecretIdTextField().getText());
        translateConfig.setTencentSecretKey(String.valueOf(translateSettingView.getTencentSecretKeyTextField().getPassword()));
        translateConfig.setVolcanoSecretId(translateSettingView.getVolcanoSecretIdTextField().getText());
        translateConfig.setVolcanoSecretKey(String.valueOf(translateSettingView.getVolcanoSecretKeyTextField().getPassword()));
        translateConfig.setXfAppId(translateSettingView.getXfAppIdTextField().getText());
        translateConfig.setXfApiKey(translateSettingView.getXfApiKeyTextField().getText());
        translateConfig.setXfApiSecret(String.valueOf(translateSettingView.getXfApiSecretTextField().getPassword()));
        translateConfig.setGoogleSecretKey(String.valueOf(translateSettingView.getGoogleSecretKeyTextField().getPassword()));
        translateConfig.setMicrosoftKey(String.valueOf(translateSettingView.getMicrosoftKeyTextField().getPassword()));
        translateConfig.setNiuApiKey(String.valueOf(translateSettingView.getNiuApiKeyTextField().getPassword()));
        translateConfig.setCaiyunToken(String.valueOf(translateSettingView.getCaiyunTokenTextField().getPassword()));
        translateConfig.setHwProjectId(translateSettingView.getHwProjectIdTextField().getText());
        translateConfig.setHwAppId(translateSettingView.getHwAppIdTextField().getText());
        translateConfig.setHwAppSecret(String.valueOf(translateSettingView.getHwAppSecretTextField().getPassword()));
        translateConfig.setThsAppId(translateSettingView.getThsAppIdTextField().getText());
        translateConfig.setThsAppSecret(String.valueOf(translateSettingView.getThsAppSecretTextField().getPassword()));
        translateConfig.setCustomApiUrl(translateSettingView.getCustomApiUrlTextField().getText());
        translateConfig.setCustomApiMaxCharLength(Integer.parseInt(translateSettingView.getCustomApiMaxCharLengthTextField().getText()));
        translateConfig.setCustomSupportLanguage(translateSettingView.getCustomSupportLanguageTextField().getText());
        translateConfig.setOpenModelChannel(String.valueOf(translateSettingView.getOpenModelComboBox().getSelectedItem()));
        translateConfig.setTyModel(String.valueOf(translateSettingView.getTyModelComboBox().getSelectedItem()));
        translateConfig.setTyKey(String.valueOf(translateSettingView.getTyKeyTextField().getPassword()));
        translateConfig.setKimiModel(String.valueOf(translateSettingView.getKimiModelComboBox().getSelectedItem()));
        translateConfig.setKimiKey(String.valueOf(translateSettingView.getKimiKeyPasswordField().getPassword()));
        translateConfig.setWenxinModel(String.valueOf(translateSettingView.getWenxinModelComboBox().getSelectedItem()));
        translateConfig.setWenxinApiKey(translateSettingView.getWenxinApiKeyTextField().getText());
        translateConfig.setWenxinApiSecret(String.valueOf(translateSettingView.getWenxinApiSecretPasswordField().getPassword()));
        translateConfig.setLibreServerUrl(String.valueOf(translateSettingView.getLibreServerUrlComboBox().getSelectedItem()));
        // 配置检查
        checkTranslateConfig();
    }

    /**
     * 检查翻译配置
     *
     * @author mabin
     * @date 2024/03/16 14:02
     */
    private void checkTranslateConfig() throws ConfigurationException {
        ValidatorUtil.notTrue(StringUtils.isBlank(translateConfig.getTranslateChannel()) || !TranslateEnum.getTranslator().contains(translateConfig.getTranslateChannel()), "请选择正确的翻译渠道");
        if (TranslateEnum.BAIDU.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getAppId(), translateConfig.getAppSecret()), TranslateEnum.BAIDU.getTranslate() + "密钥不能为空");
            if (Boolean.TRUE.equals(translateConfig.getBaiduDomainCheckBox())) {
                ValidatorUtil.notBlank(translateConfig.getBaiduDomainComboBox(), TranslateEnum.BAIDU.getTranslate() + "领域不能为空");
            }
        }
        if (TranslateEnum.ALIYUN.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getAccessKeyId(), translateConfig.getAccessKeySecret()), TranslateEnum.ALIYUN.getTranslate() + "密钥不能为空");
            if (Boolean.TRUE.equals(translateConfig.getAliyunDomainCheckBox())) {
                ValidatorUtil.notBlank(translateConfig.getAliyunDomainComboBox(), TranslateEnum.ALIYUN.getTranslate() + "领域不能为空");
            }
        }
        if (TranslateEnum.YOUDAO.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getSecretId(), translateConfig.getSecretKey()), TranslateEnum.YOUDAO.getTranslate() + "密钥不能为空");
            if (Boolean.TRUE.equals(translateConfig.getYoudaoDomainCheckBox())) {
                ValidatorUtil.notBlank(translateConfig.getYoudaoDomainComboBox(), TranslateEnum.YOUDAO.getTranslate() + "领域不能为空");
            }
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
        if (TranslateEnum.CUSTOM.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getCustomApiUrl(), translateConfig.getCustomSupportLanguage())
                    && Objects.nonNull(translateConfig.getCustomApiMaxCharLength()), TranslateEnum.CUSTOM.getTranslate() + "配置不能为空");
        }
        if (TranslateEnum.OPEN_BIG_MODEL.getTranslate().equals(translateConfig.getTranslateChannel())) {
            if (OpenModelTranslateEnum.TONG_YI.getModel().equals(translateConfig.getOpenModelChannel())) {
                ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getTyKey(), translateConfig.getTyModel()), OpenModelTranslateEnum.TONG_YI.getModel() + "配置不能为空");
            }
            if (OpenModelTranslateEnum.KIMI.getModel().equals(translateConfig.getOpenModelChannel())) {
                ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getKimiKey(), translateConfig.getKimiModel()), OpenModelTranslateEnum.KIMI.getModel() + "配置不能为空");
            }
            if (OpenModelTranslateEnum.WEN_XIN.getModel().equals(translateConfig.getOpenModelChannel())) {
                ValidatorUtil.isTrue(StringUtils.isNoneBlank(translateConfig.getWenxinApiKey(), translateConfig.getWenxinApiSecret(),
                        translateConfig.getWenxinModel()), OpenModelTranslateEnum.KIMI.getModel() + "配置不能为空");
            }
        }
        if (TranslateEnum.LIBRE.getTranslate().equals(translateConfig.getTranslateChannel())) {
            ValidatorUtil.isTrue(Objects.nonNull(translateSettingView.getLibreServerUrlComboBox().getSelectedItem()), TranslateEnum.LIBRE.getTranslate() + "翻译URL不能为空");
        }
    }

}

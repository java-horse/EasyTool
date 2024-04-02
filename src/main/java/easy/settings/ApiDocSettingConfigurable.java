package easy.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.base.Constants;
import easy.config.api.ApiDocConfig;
import easy.config.api.ApiDocConfigComponent;
import easy.form.api.ApiDocSettingView;
import easy.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ApiDocSettingConfigurable implements Configurable {

    private ApiDocConfig apiDocConfig = ApplicationManager.getApplication().getService(ApiDocConfigComponent.class).getState();
    private ApiDocSettingView apiDocSettingView = new ApiDocSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ApiDoc";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return apiDocSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !StringUtils.equals(apiDocConfig.getYapiServer(), apiDocSettingView.getYapiServerTextField().getText())
                || !StringUtils.equals(apiDocConfig.getYapiToken(), apiDocSettingView.getYapiTokenTextField().getText())
                || !Objects.equals(apiDocConfig.getYapiEnable(), apiDocSettingView.getYapiEnableCheckBox().isSelected())
                || !StringUtils.equals(apiDocConfig.getApifoxServer(), apiDocSettingView.getApifoxServerTextField().getText())
                || !StringUtils.equals(apiDocConfig.getApifoxToken(), apiDocSettingView.getApifoxTokenTextField().getText())
                || !Objects.equals(apiDocConfig.getApifoxEnable(), apiDocSettingView.getApifoxEnableCheckBox().isSelected());
    }

    @Override
    public void reset() {
        apiDocSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        // 参数校验
        if (Objects.nonNull(apiDocSettingView.getYapiEnableCheckBox()) && apiDocSettingView.getYapiEnableCheckBox().isSelected()) {
            ValidatorUtil.isTrue(StringUtils.startsWithAny(apiDocSettingView.getYapiServerTextField().getText(),
                    Constants.HTTP, Constants.HTTPS), String.format("YApi Server 请以%s或%s为请求协议", Constants.HTTP, Constants.HTTPS));
        }
        if (Objects.nonNull(apiDocSettingView.getApifoxEnableCheckBox()) && apiDocSettingView.getApifoxEnableCheckBox().isSelected()) {
            ValidatorUtil.isTrue(StringUtils.startsWithAny(apiDocSettingView.getApifoxServerTextField().getText(),
                    Constants.HTTP, Constants.HTTPS), String.format("ApiFox Server 请以%s或%s为请求协议", Constants.HTTP, Constants.HTTPS));
        }
        // 参数值设置
        apiDocConfig.setYapiServer(apiDocSettingView.getYapiServerTextField().getText());
        apiDocConfig.setYapiToken(apiDocSettingView.getYapiTokenTextField().getText());
        apiDocConfig.setYapiEnable(apiDocSettingView.getYapiEnableCheckBox().isSelected());
        apiDocConfig.setApifoxServer(apiDocSettingView.getApifoxServerTextField().getText());
        apiDocConfig.setApifoxToken(apiDocSettingView.getApifoxTokenTextField().getText());
        apiDocConfig.setApifoxEnable(apiDocSettingView.getApifoxEnableCheckBox().isSelected());
    }

}

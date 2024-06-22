package easy.settings.api;

import cn.hutool.core.map.MapUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.base.Constants;
import easy.config.api.YApiConfig;
import easy.config.api.YApiConfigComponent;
import easy.enums.ApiDocTypeEnum;
import easy.form.api.YApiSettingView;
import easy.helper.ServiceHelper;
import easy.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.TreeMap;

public class YApiSettingConfigurable implements Configurable {

    private YApiConfig yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();
    private YApiSettingView yApiSettingView = new YApiSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return ApiDocTypeEnum.YAPI.getTitle();
    }

    @Override
    public @Nullable JComponent createComponent() {
        return yApiSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(yApiConfig.getApiEnable(), yApiSettingView.getYapiEnableButton().isSelected())
                || !StringUtils.equals(yApiConfig.getApiServerUrl(), yApiSettingView.getYapiServerUrlTextField().getText())
                || yApiSettingView.getYapiTable().getRowCount() != yApiConfig.getYapiTableMap().size();
    }

    @Override
    public void reset() {
        yApiSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        yApiConfig.setApiEnable(yApiSettingView.getYapiEnableButton().isSelected());
        yApiConfig.setApiServerUrl(yApiSettingView.getYapiServerUrlTextField().getText());
        if (MapUtil.isEmpty(yApiConfig.getYapiTableMap())) {
            yApiConfig.setYapiTableMap(new TreeMap<>());
        }
        ValidatorUtil.isTrue(StringUtils.startsWithAny(yApiConfig.getApiServerUrl(), Constants.HTTPS, Constants.HTTP),
                String.format("【%s】必须以%s或%s开头", yApiSettingView.getYapiServerUrlLabel().getText(), Constants.HTTPS, Constants.HTTP),
                Constants.PLUGIN_NAME);
    }

}

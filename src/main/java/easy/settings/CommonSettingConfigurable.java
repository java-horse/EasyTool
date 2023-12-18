package easy.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.form.CommonSettingView;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 通用设置
 *
 * @project: EasyTool
 * @package: easy.settings
 * @author: mabin
 * @date: 2023/12/17 11:52:08
 */
public class CommonSettingConfigurable implements Configurable {

    private CommonConfig commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();
    private CommonSettingView commonSettingView = new CommonSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Common";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return commonSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(commonConfig.getSwaggerConfirmYesCheckBox(), commonSettingView.getSwaggerConfirmYesCheckBox().isSelected())
                || !Objects.equals(commonConfig.getSwaggerConfirmNoCheckBox(), commonSettingView.getSwaggerConfirmNoCheckBox().isSelected())
                || !Objects.equals(commonConfig.getSearchApiDefaultIconRadioButton(), commonSettingView.getSearchApiDefaultIconRadioButton().isSelected())
                || !Objects.equals(commonConfig.getSearchApiCuteIconRadioButton(), commonSettingView.getSearchApiCuteIconRadioButton().isSelected());
    }

    @Override
    public void reset() {
        commonSettingView.reset();
    }

    @Override
    public void apply() {
        commonConfig.setSwaggerConfirmYesCheckBox(commonSettingView.getSwaggerConfirmYesCheckBox().isSelected());
        commonConfig.setSwaggerConfirmNoCheckBox(commonSettingView.getSwaggerConfirmNoCheckBox().isSelected());
        commonConfig.setSearchApiDefaultIconRadioButton(commonSettingView.getSearchApiDefaultIconRadioButton().isSelected());
        commonConfig.setSearchApiCuteIconRadioButton(commonSettingView.getSearchApiCuteIconRadioButton().isSelected());
    }

}

package easy.settings;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.form.CommonSettingView;
import easy.helper.ServiceHelper;
import easy.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
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

    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();
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
                || !Objects.equals(commonConfig.getSearchApiCuteIconRadioButton(), commonSettingView.getSearchApiCuteIconRadioButton().isSelected())
                || !Objects.equals(commonConfig.getRestfulDisplayApiCommentCheckBox(), commonSettingView.getRestfulDisplayApiCommentCheckBox().isSelected())
                || !Objects.equals(commonConfig.getTranslateConfirmInputModelYesCheckBox(), commonSettingView.getTranslateConfirmInputModelYesCheckBox().isSelected())
                || !Objects.equals(commonConfig.getTranslateConfirmInputModelNoCheckBox(), commonSettingView.getTranslateConfirmInputModelNoCheckBox().isSelected())
                || !Objects.equals(commonConfig.getTabHighlightEnableCheckBox(), commonSettingView.getTabHighlightEnableCheckBox().isSelected())
                || (Objects.isNull(commonConfig.getPersistentColor()) || Objects.isNull(commonSettingView.getTabBackgroundColorPanel())
                || !Objects.equals(commonConfig.getPersistentColor().getRed(), commonSettingView.getTabBackgroundColorPanel().getSelectedColor().getRed())
                || !Objects.equals(commonConfig.getPersistentColor().getGreen(), commonSettingView.getTabBackgroundColorPanel().getSelectedColor().getGreen())
                || !Objects.equals(commonConfig.getPersistentColor().getBlue(), commonSettingView.getTabBackgroundColorPanel().getSelectedColor().getBlue()))
                || !Objects.equals(commonConfig.getTabHighlightSizeComboBox(), commonSettingView.getTabHighlightSizeComboBox().getSelectedItem())
                || !StringUtils.equals(commonConfig.getTabHighlightGradientStepFormattedTextField(), commonSettingView.getTabHighlightGradientStepFormattedTextField().getText())
                || !Objects.equals(commonConfig.getConvertCharEnableCheckBox(), commonSettingView.getConvertCharEnableCheckBox().isSelected())
                || !Objects.equals(commonConfig.getPluginAutoUpdateEnable(), commonSettingView.getPluginAutoUpdateEnableButton().isSelected());
    }

    @Override
    public void reset() {
        commonSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        commonConfig.setSwaggerConfirmYesCheckBox(commonSettingView.getSwaggerConfirmYesCheckBox().isSelected());
        commonConfig.setSwaggerConfirmNoCheckBox(commonSettingView.getSwaggerConfirmNoCheckBox().isSelected());
        commonConfig.setSearchApiDefaultIconRadioButton(commonSettingView.getSearchApiDefaultIconRadioButton().isSelected());
        commonConfig.setSearchApiCuteIconRadioButton(commonSettingView.getSearchApiCuteIconRadioButton().isSelected());
        commonConfig.setRestfulDisplayApiCommentCheckBox(commonSettingView.getRestfulDisplayApiCommentCheckBox().isSelected());
        commonConfig.setTranslateConfirmInputModelYesCheckBox(commonSettingView.getTranslateConfirmInputModelYesCheckBox().isSelected());
        commonConfig.setTranslateConfirmInputModelNoCheckBox(commonSettingView.getTranslateConfirmInputModelNoCheckBox().isSelected());
        commonConfig.setTabHighlightEnableCheckBox(commonSettingView.getTabHighlightEnableCheckBox().isSelected());
        Color color = commonSettingView.getTabBackgroundColorPanel().getSelectedColor();
        CommonConfig.PersistentColor persistentColor = new CommonConfig.PersistentColor();
        persistentColor.setRed(color.getRed());
        persistentColor.setGreen(color.getGreen());
        persistentColor.setBlue(color.getBlue());
        commonConfig.setPersistentColor(persistentColor);
        commonConfig.setTabHighlightSizeComboBox(String.valueOf(commonSettingView.getTabHighlightSizeComboBox().getSelectedItem()));
        String tabHighlightGradientStepFormattedTextField = commonSettingView.getTabHighlightGradientStepFormattedTextField().getText();
        commonConfig.setTabHighlightGradientStepFormattedTextField(tabHighlightGradientStepFormattedTextField);
        ValidatorUtil.isTrue(StringUtils.isNotBlank(tabHighlightGradientStepFormattedTextField)
                        && NumberUtil.isInteger(tabHighlightGradientStepFormattedTextField)
                        && Convert.toInt(tabHighlightGradientStepFormattedTextField) >= Constants.NUM.TEN
                        && Convert.toInt(tabHighlightGradientStepFormattedTextField) <= Constants.NUM.EIGHTY,
                String.format("属性: %s 请输入合法的整数(x>=%d && x<=%d)", "Tab Highlight Gradient Step", Constants.NUM.TEN, Constants.NUM.EIGHTY));
        commonConfig.setConvertCharEnableCheckBox(commonSettingView.getConvertCharEnableCheckBox().isSelected());
        commonConfig.setPluginAutoUpdateEnable(commonSettingView.getPluginAutoUpdateEnableButton().isSelected());
    }

}

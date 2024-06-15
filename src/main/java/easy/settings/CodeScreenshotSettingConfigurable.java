package easy.settings;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.form.CodeScreenshotSettingView;
import easy.handler.ServiceHelper;
import easy.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class CodeScreenshotSettingConfigurable implements Configurable {
    private CodeScreenshotConfig codeScreenshotConfig = ServiceHelper.getService(CodeScreenshotConfigComponent.class).getState();
    private CodeScreenshotSettingView codeScreenshotSettingView = new CodeScreenshotSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "CodeScreenshot";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return codeScreenshotSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(Convert.toInt(codeScreenshotConfig.getScale()),
                codeScreenshotSettingView.getScaleSlider().getValue())
                || !Objects.equals(Convert.toInt(codeScreenshotConfig.getInnerPadding()),
                codeScreenshotSettingView.getInnerPaddingSlider().getValue())
                || !Objects.equals(Convert.toInt(codeScreenshotConfig.getOuterPadding()),
                codeScreenshotSettingView.getOuterPaddingSlider().getValue())
                || !Objects.equals(codeScreenshotConfig.getWindowRoundness(),
                codeScreenshotSettingView.getWindowRoundnessSlider().getValue())
                || !Objects.equals(codeScreenshotConfig.getBackgroundColor(),
                codeScreenshotSettingView.getInitBackgroundColor().getRGB())
                || !Objects.equals(codeScreenshotConfig.getRemoveIndentation(),
                codeScreenshotSettingView.getRemoveIndentationCheckBox().isSelected())
                || !Objects.equals(codeScreenshotConfig.getShowWindowIcons(),
                codeScreenshotSettingView.getShowWindowIconsCheckBox().isSelected())
                || !Objects.equals(codeScreenshotConfig.getAutoCopyPayboard(),
                codeScreenshotSettingView.getAutoCopyPayboardCheckBox().isSelected())
                || !StringUtils.equals(codeScreenshotConfig.getCustomFileName(),
                codeScreenshotSettingView.getCustomFileNameTextField().getText())
                || !Objects.equals(codeScreenshotConfig.getCustomFileNameFormat(),
                codeScreenshotSettingView.getCustomFileNameFormatComboBox().getSelectedItem())
                || !Objects.equals(codeScreenshotConfig.getCustomFileNameSuffix(),
                codeScreenshotSettingView.getCustomFileNameSuffixComboBox().getSelectedItem())
                || !Objects.equals(codeScreenshotConfig.getWaterMarkFontFamily(),
                codeScreenshotSettingView.getFontFamilyFontComboBox().getSelectedItem())
                || !Objects.equals(codeScreenshotConfig.getWaterMarkFontStyle(),
                codeScreenshotSettingView.getFontStyleComboBox().getSelectedItem())
                || !StringUtils.equals(codeScreenshotConfig.getWaterMarkFontSize(),
                codeScreenshotSettingView.getFontSizeTextField().getText())
                || !StringUtils.equals(codeScreenshotConfig.getWaterMarkFontText(),
                codeScreenshotSettingView.getFontWaterMarkTextField().getText())
                || !Objects.equals(codeScreenshotConfig.getWaterMarkFontColor(),
                codeScreenshotSettingView.getInitFontColor().getRGB())
                || !Objects.equals(codeScreenshotConfig.getAutoAddWaterMark(),
                codeScreenshotSettingView.getAutoAddWaterMarkCheckBox().isSelected())
                || !Objects.equals(codeScreenshotConfig.getAutoFullScreenWatermark(),
                codeScreenshotSettingView.getAutoFullScreenWatermarkCheckBox().isSelected())
                || !Objects.equals(codeScreenshotConfig.getFontWaterMarkTransparency(),
                codeScreenshotSettingView.getFontWaterMarkTransparencySlider().getValue())
                || !Objects.equals(codeScreenshotConfig.getFontWaterMarkSparsity(),
                codeScreenshotSettingView.getFontWaterMarkSparsitySlider().getValue())
                || !Objects.equals(codeScreenshotConfig.getFontWaterMarkRotate(),
                codeScreenshotSettingView.getFontWaterMarkRotateSlider().getValue());


    }

    @Override
    public void reset() {
        codeScreenshotSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        // 赋值
        codeScreenshotConfig.setScale(codeScreenshotSettingView.getScaleSlider().getValue() * CodeScreenshotSettingView.SLIDER_SCALE);
        codeScreenshotConfig.setInnerPadding(Convert.toDouble(codeScreenshotSettingView.getInnerPaddingSlider().getValue()));
        codeScreenshotConfig.setOuterPadding(Convert.toDouble(codeScreenshotSettingView.getOuterPaddingSlider().getValue()));
        codeScreenshotConfig.setWindowRoundness(codeScreenshotSettingView.getWindowRoundnessSlider().getValue());
        codeScreenshotConfig.setRemoveIndentation(codeScreenshotSettingView.getRemoveIndentationCheckBox().isSelected());
        codeScreenshotConfig.setShowWindowIcons(codeScreenshotSettingView.getShowWindowIconsCheckBox().isSelected());
        codeScreenshotConfig.setBackgroundColor(codeScreenshotSettingView.getInitBackgroundColor().getRGB());
        codeScreenshotConfig.setAutoCopyPayboard(codeScreenshotSettingView.getAutoCopyPayboardCheckBox().isSelected());
        codeScreenshotConfig.setCustomFileName(codeScreenshotSettingView.getCustomFileNameTextField().getText());
        codeScreenshotConfig.setCustomFileNameFormat(String.valueOf(codeScreenshotSettingView.getCustomFileNameFormatComboBox().getSelectedItem()));
        codeScreenshotConfig.setCustomFileNameSuffix(String.valueOf(codeScreenshotSettingView.getCustomFileNameSuffixComboBox().getSelectedItem()));
        codeScreenshotConfig.setWaterMarkFontFamily(String.valueOf(codeScreenshotSettingView.getFontFamilyFontComboBox().getSelectedItem()));
        codeScreenshotConfig.setWaterMarkFontStyle(String.valueOf(codeScreenshotSettingView.getFontStyleComboBox().getSelectedItem()));
        codeScreenshotConfig.setWaterMarkFontSize(codeScreenshotSettingView.getFontSizeTextField().getText());
        codeScreenshotConfig.setWaterMarkFontText(codeScreenshotSettingView.getFontWaterMarkTextField().getText());
        codeScreenshotConfig.setWaterMarkFontColor(codeScreenshotSettingView.getInitFontColor().getRGB());
        codeScreenshotConfig.setAutoAddWaterMark(codeScreenshotSettingView.getAutoAddWaterMarkCheckBox().isSelected());
        codeScreenshotConfig.setAutoFullScreenWatermark(codeScreenshotSettingView.getAutoFullScreenWatermarkCheckBox().isSelected());
        codeScreenshotConfig.setFontWaterMarkTransparency(codeScreenshotSettingView.getFontWaterMarkTransparencySlider().getValue());
        codeScreenshotConfig.setFontWaterMarkSparsity(codeScreenshotSettingView.getFontWaterMarkSparsitySlider().getValue());
        codeScreenshotConfig.setFontWaterMarkRotate(codeScreenshotSettingView.getFontWaterMarkRotateSlider().getValue());
        // 校验
        ValidatorUtil.isTrue(NumberUtil.isNumber(codeScreenshotConfig.getWaterMarkFontSize()), String.format("Font " +
                "Size 的值【%s】非合法整数, 请重新配置", codeScreenshotConfig.getWaterMarkFontSize()));
    }

}

package easy.settings;

import cn.hutool.core.convert.Convert;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.form.CodeScreenshotSettingView;
import easy.handler.ServiceHelper;
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
        return !Objects.equals(Convert.toInt(codeScreenshotConfig.getScale()), codeScreenshotSettingView.getScaleSlider().getValue()) || !Objects.equals(Convert.toInt(codeScreenshotConfig.getInnerPadding()), codeScreenshotSettingView.getInnerPaddingSlider().getValue()) || !Objects.equals(Convert.toInt(codeScreenshotConfig.getOuterPadding()), codeScreenshotSettingView.getOuterPaddingSlider().getValue()) || !Objects.equals(codeScreenshotConfig.getWindowRoundness(), codeScreenshotSettingView.getWindowRoundnessSlider().getValue()) || !Objects.equals(codeScreenshotConfig.getBackgroundColor(), codeScreenshotSettingView.getInitBackgroundColor().getRGB()) || !Objects.equals(codeScreenshotConfig.getRemoveIndentation(), codeScreenshotSettingView.getRemoveIndentationCheckBox().isSelected()) || !Objects.equals(codeScreenshotConfig.getShowWindowIcons(), codeScreenshotSettingView.getShowWindowIconsCheckBox().isSelected());
    }

    @Override
    public void reset() {
        codeScreenshotSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        codeScreenshotConfig.setScale(codeScreenshotSettingView.getScaleSlider().getValue() * CodeScreenshotSettingView.SLIDER_SCALE);
        codeScreenshotConfig.setInnerPadding(Convert.toDouble(codeScreenshotSettingView.getInnerPaddingSlider().getValue()));
        codeScreenshotConfig.setOuterPadding(Convert.toDouble(codeScreenshotSettingView.getOuterPaddingSlider().getValue()));
        codeScreenshotConfig.setWindowRoundness(codeScreenshotSettingView.getWindowRoundnessSlider().getValue());
        codeScreenshotConfig.setRemoveIndentation(codeScreenshotSettingView.getRemoveIndentationCheckBox().isSelected());
        codeScreenshotConfig.setShowWindowIcons(codeScreenshotSettingView.getShowWindowIconsCheckBox().isSelected());
        codeScreenshotConfig.setBackgroundColor(codeScreenshotSettingView.getInitBackgroundColor().getRGB());
    }

}

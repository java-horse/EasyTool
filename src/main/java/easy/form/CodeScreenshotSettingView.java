package easy.form;

import cn.hutool.core.convert.Convert;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.ui.ColorChooserService;
import com.intellij.ui.JBColor;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.handler.ServiceHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Objects;

public class CodeScreenshotSettingView {

    /**
     * 滑块刻度
     */
    public static final double SLIDER_SCALE = 0.25;
    private CodeScreenshotConfig codeScreenshotConfig = ServiceHelper.getService(CodeScreenshotConfigComponent.class).getState();


    private JPanel panel;
    private JSlider scaleSlider;
    private JLabel scaleSliderValueLabel;
    private JLabel scaleLabel;
    private JLabel innerPaddingLabel;
    private JSlider innerPaddingSlider;
    private JLabel innerPaddingValueLabel;
    private JLabel outerPaddingLabel;
    private JSlider outerPaddingSlider;
    private JLabel outerPaddingValueLabel;
    private JLabel windowRoundnessLabel;
    private JSlider windowRoundnessSlider;
    private JLabel windowRoundnessValueLabel;
    private JFormattedTextField formattedTextField;
    private JButton colorButton;
    private JCheckBox removeIndentationCheckBox;
    private JCheckBox showWindowIconsCheckBox;
    private JCheckBox autoCopyPayboardCheckBox;
    private JTextField customFileNameTextField;
    private JComboBox customFileNameFormatComboBox;
    private JComboBox customFileNameSuffixComboBox;
    private JLabel customFileNameLabel;
    private Color initBackgroundColor = new JBColor(new Color(171, 184, 195), new Color(171, 184, 195));

    public CodeScreenshotSettingView() {
        scaleSlider.addChangeListener(e -> scaleSliderValueLabel.setText(String.format("%.2f", scaleSlider.getValue() * SLIDER_SCALE)));
        innerPaddingSlider.addChangeListener(e -> innerPaddingValueLabel.setText(Integer.toString(innerPaddingSlider.getValue())));
        outerPaddingSlider.addChangeListener(e -> outerPaddingValueLabel.setText(Integer.toString(outerPaddingSlider.getValue())));
        windowRoundnessSlider.addChangeListener(e -> windowRoundnessValueLabel.setText(Integer.toString(windowRoundnessSlider.getValue())));
        colorButton.addActionListener(e -> {
            Color chooseBackgroundColor = ColorChooserService.getInstance().showDialog(ProjectManagerEx.getInstanceEx().getDefaultProject(), panel,
                    "Choose Background Color", initBackgroundColor, true, Collections.emptyList(), true);
            if (Objects.nonNull(chooseBackgroundColor)) {
                initBackgroundColor = chooseBackgroundColor;
            }
            updateBackgroundColorText();
        });
        updateBackgroundColorText();
    }


    private void createUIComponents() {
        codeScreenshotConfig = ServiceHelper.getService(CodeScreenshotConfigComponent.class).getState();
    }

    public void reset() {
        setScaleSlider(codeScreenshotConfig.getScale());
        setInnerPaddingSlider(codeScreenshotConfig.getInnerPadding());
        setOuterPaddingSlider(codeScreenshotConfig.getOuterPadding());
        setWindowRoundnessSlider(codeScreenshotConfig.getWindowRoundness());
        setRemoveIndentationCheckBox(codeScreenshotConfig.getRemoveIndentation());
        setShowWindowIconsCheckBox(codeScreenshotConfig.getShowWindowIcons());
        setInitBackgroundColor(new JBColor(new Color(codeScreenshotConfig.getBackgroundColor(), true), new Color(codeScreenshotConfig.getBackgroundColor(), true)));
        updateBackgroundColorText();
        setAutoCopyPayboardCheckBox(codeScreenshotConfig.getAutoCopyPayboard());
        setCustomFileNameTextField(codeScreenshotConfig.getCustomFileName());
        setCustomFileNameFormatComboBox(codeScreenshotConfig.getCustomFileNameFormat());
        setCustomFileNameSuffixComboBox(codeScreenshotConfig.getCustomFileNameSuffix());
    }

    private void updateBackgroundColorText() {
        formattedTextField.setText(String.format("A: %02.0f%%, R: %02.0f%%, G: %02.0f%%, B: %02.0f%%", initBackgroundColor.getAlpha() / 255f * 100, initBackgroundColor.getRed() / 255f * 100, initBackgroundColor.getGreen() / 255f * 100, initBackgroundColor.getBlue() / 255f * 100));
        formattedTextField.setForeground(new JBColor(new Color(initBackgroundColor.getRed(), initBackgroundColor.getGreen(), initBackgroundColor.getBlue()), new Color(initBackgroundColor.getRed(), initBackgroundColor.getGreen(), initBackgroundColor.getBlue())));
    }

    public JComponent getComponent() {
        return panel;
    }

    public JSlider getScaleSlider() {
        return scaleSlider;
    }

    public void setScaleSlider(Double scaleSlider) {
        this.scaleSlider.setValue(Convert.toInt(Math.round(scaleSlider / SLIDER_SCALE)));
    }

    public JSlider getInnerPaddingSlider() {
        return innerPaddingSlider;
    }

    public void setInnerPaddingSlider(Double innerPaddingSlider) {
        this.innerPaddingSlider.setValue(Convert.toInt(Math.round(innerPaddingSlider)));
    }

    public JSlider getOuterPaddingSlider() {
        return outerPaddingSlider;
    }

    public void setOuterPaddingSlider(Double outerPaddingSlider) {
        this.outerPaddingSlider.setValue(Convert.toInt(Math.round(outerPaddingSlider)));
    }

    public JSlider getWindowRoundnessSlider() {
        return windowRoundnessSlider;
    }

    public void setWindowRoundnessSlider(Integer windowRoundnessSlider) {
        this.windowRoundnessSlider.setValue(windowRoundnessSlider);
    }

    public JCheckBox getRemoveIndentationCheckBox() {
        return removeIndentationCheckBox;
    }

    public void setRemoveIndentationCheckBox(Boolean removeIndentationCheckBox) {
        this.removeIndentationCheckBox.setSelected(removeIndentationCheckBox);
    }

    public JCheckBox getShowWindowIconsCheckBox() {
        return showWindowIconsCheckBox;
    }

    public void setShowWindowIconsCheckBox(Boolean showWindowIconsCheckBox) {
        this.showWindowIconsCheckBox.setSelected(showWindowIconsCheckBox);
    }

    public Color getInitBackgroundColor() {
        return initBackgroundColor;
    }

    public void setInitBackgroundColor(Color initBackgroundColor) {
        this.initBackgroundColor = initBackgroundColor;
    }

    public JCheckBox getAutoCopyPayboardCheckBox() {
        return autoCopyPayboardCheckBox;
    }

    public void setAutoCopyPayboardCheckBox(Boolean autoCopyPayboardCheckBox) {
        this.autoCopyPayboardCheckBox.setSelected(autoCopyPayboardCheckBox);
    }

    public JTextField getCustomFileNameTextField() {
        return customFileNameTextField;
    }

    public void setCustomFileNameTextField(String customFileNameTextField) {
        this.customFileNameTextField.setText(customFileNameTextField);
    }

    public JComboBox getCustomFileNameFormatComboBox() {
        return customFileNameFormatComboBox;
    }

    public void setCustomFileNameFormatComboBox(String customFileNameFormatComboBox) {
        this.customFileNameFormatComboBox.setSelectedItem(customFileNameFormatComboBox);
    }

    public JComboBox getCustomFileNameSuffixComboBox() {
        return customFileNameSuffixComboBox;
    }

    public void setCustomFileNameSuffixComboBox(String customFileNameSuffixComboBox) {
        this.customFileNameSuffixComboBox.setSelectedItem(customFileNameSuffixComboBox);
    }

}

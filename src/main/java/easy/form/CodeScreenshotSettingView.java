package easy.form;

import cn.hutool.core.convert.Convert;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.ui.ColorChooserService;
import com.intellij.ui.FontComboBox;
import com.intellij.ui.JBColor;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
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
    private FontComboBox fontFamilyFontComboBox;
    private JTextField fontSizeTextField;
    private JComboBox fontStyleComboBox;
    private JTextField fontWaterMarkTextField;
    private JLabel fontFamilyLabel;
    private JLabel fontSizeLabel;
    private JLabel fontStyleLabel;
    private JLabel fontWaterMarkLabel;
    private JPanel filePanel;
    private JTextField fontColorTextField;
    private JButton fontColorButton;
    private JLabel fontColorLabel;
    private JCheckBox autoAddWaterMarkCheckBox;
    private JCheckBox autoFullScreenWatermarkCheckBox;
    private JSlider fontWaterMarkTransparencySlider;
    private JLabel fontWaterMarkTransparencyLabel;
    private JLabel fontWaterMarkTransparencyValueLabel;
    private JLabel fontWaterMarkRotateLabel;
    private JSlider fontWaterMarkRotateSlider;
    private JLabel fontWaterMarkRotateValueLabel;
    private JLabel fontWaterMarkSparsityLabel;
    private JSlider fontWaterMarkSparsitySlider;
    private JLabel fontWaterMarkSparsityValueLabel;

    private Color initBackgroundColor = new JBColor(new Color(171, 184, 195), new Color(171, 184, 195));
    private Color initFontColor = new JBColor(new Color(171, 184, 195), new Color(171, 184, 195));

    public CodeScreenshotSettingView() {
        scaleSlider.addChangeListener(e -> scaleSliderValueLabel.setText(String.format("%.2f", scaleSlider.getValue() * SLIDER_SCALE)));
        innerPaddingSlider.addChangeListener(e -> innerPaddingValueLabel.setText(Integer.toString(innerPaddingSlider.getValue())));
        outerPaddingSlider.addChangeListener(e -> outerPaddingValueLabel.setText(Integer.toString(outerPaddingSlider.getValue())));
        windowRoundnessSlider.addChangeListener(e -> windowRoundnessValueLabel.setText(Integer.toString(windowRoundnessSlider.getValue())));
        fontWaterMarkTransparencySlider.addChangeListener(e -> fontWaterMarkTransparencyValueLabel.setText(Integer.toString(fontWaterMarkTransparencySlider.getValue())));
        fontWaterMarkRotateSlider.addChangeListener(e -> fontWaterMarkRotateValueLabel.setText(Integer.toString(fontWaterMarkRotateSlider.getValue())));
        fontWaterMarkSparsitySlider.addChangeListener(e -> fontWaterMarkSparsityValueLabel.setText(Integer.toString(fontWaterMarkSparsitySlider.getValue())));
        colorButton.addActionListener(e -> {
            Color chooseBackgroundColor = ColorChooserService.getInstance().showDialog(ProjectManagerEx.getInstanceEx().getDefaultProject(), panel,
                    "Choose Background Color", initBackgroundColor, true, Collections.emptyList(), true);
            if (Objects.nonNull(chooseBackgroundColor)) {
                initBackgroundColor = chooseBackgroundColor;
            }
            updateBackgroundColorText();
        });
        updateBackgroundColorText();
        fontColorButton.addActionListener(e -> {
            Color chooseFontColor = ColorChooserService.getInstance().showDialog(ProjectManagerEx.getInstanceEx().getDefaultProject(), panel,
                    "Choose Font Color", initFontColor, true, Collections.emptyList(), true);
            if (Objects.nonNull(chooseFontColor)) {
                initFontColor = chooseFontColor;
            }
            updateFontColorText();
        });
        updateFontColorText();
        EasyCommonUtil.customBackgroundText(fontWaterMarkTextField, "请输入水印字符...");
        autoAddWaterMarkCheckBox.addItemListener(e -> {
            boolean selectResult = e.getStateChange() == ItemEvent.SELECTED;
            fontFamilyFontComboBox.setEnabled(selectResult);
            fontSizeTextField.setEnabled(selectResult);
            fontStyleComboBox.setEnabled(selectResult);
            fontWaterMarkTextField.setEnabled(selectResult);
            fontColorTextField.setEnabled(selectResult);
            fontColorButton.setEnabled(selectResult);
            fontWaterMarkTransparencySlider.setEnabled(selectResult);
            fontWaterMarkSparsitySlider.setEnabled(selectResult);
        });
        fontWaterMarkRotateSlider.setEnabled(false);
        fontWaterMarkRotateSlider.setEnabled(false);
        fontWaterMarkRotateValueLabel.setEnabled(false);
        autoFullScreenWatermarkCheckBox.addItemListener(e -> {
            boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
            fontWaterMarkRotateLabel.setEnabled(enabled);
            fontWaterMarkRotateSlider.setEnabled(enabled);
            fontWaterMarkRotateValueLabel.setEnabled(enabled);
        });
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
        setFontFamilyFontComboBox(codeScreenshotConfig.getWaterMarkFontFamily());
        setFontSizeTextField(codeScreenshotConfig.getWaterMarkFontSize());
        setFontStyleComboBox(codeScreenshotConfig.getWaterMarkFontStyle());
        setFontWaterMarkTextField(codeScreenshotConfig.getWaterMarkFontText());
        setInitFontColor(new JBColor(new Color(codeScreenshotConfig.getWaterMarkFontColor(), true), new Color(codeScreenshotConfig.getWaterMarkFontColor(), true)));
        updateFontColorText();
        setAutoAddWaterMarkCheckBox(codeScreenshotConfig.getAutoAddWaterMark());
        setAutoFullScreenWatermarkCheckBox(codeScreenshotConfig.getAutoFullScreenWatermark());
        setFontWaterMarkTransparencySlider(codeScreenshotConfig.getFontWaterMarkTransparency());
        setFontWaterMarkRotateSlider(codeScreenshotConfig.getFontWaterMarkRotate());
        setFontWaterMarkSparsitySlider(codeScreenshotConfig.getFontWaterMarkSparsity());
    }

    /**
     * 更新背景颜色文本
     *
     * @author mabin
     * @date 2024/06/12 10:59
     */
    private void updateBackgroundColorText() {
        formattedTextField.setText(String.format("Hex: %s, A: %02.0f%%, R: %02.0f%%, G: %02.0f%%, B: %02.0f%%", String.format("#%06X", (0xFFFFFF & initBackgroundColor.getRGB())),
                initBackgroundColor.getAlpha() / 255f * 100, initBackgroundColor.getRed() / 255f * 100, initBackgroundColor.getGreen() / 255f * 100, initBackgroundColor.getBlue() / 255f * 100));
        formattedTextField.setForeground(new JBColor(new Color(initBackgroundColor.getRed(), initBackgroundColor.getGreen(), initBackgroundColor.getBlue()), new Color(initBackgroundColor.getRed(), initBackgroundColor.getGreen(), initBackgroundColor.getBlue())));
    }

    /**
     * 更新字体颜色文本
     *
     * @author mabin
     * @date 2024/06/12 10:59
     */
    private void updateFontColorText() {
        fontColorTextField.setText(String.format("#%06X", (0xFFFFFF & initFontColor.getRGB())));
        fontColorTextField.setForeground(new JBColor(new Color(initFontColor.getRed(), initFontColor.getGreen(),
                initFontColor.getBlue()), new Color(initFontColor.getRed(), initFontColor.getGreen(), initFontColor.getBlue())));
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

    public FontComboBox getFontFamilyFontComboBox() {
        return fontFamilyFontComboBox;
    }

    public void setFontFamilyFontComboBox(String fontFamilyFontComboBox) {
        this.fontFamilyFontComboBox.setFontName(fontFamilyFontComboBox);
    }

    public JTextField getFontSizeTextField() {
        return fontSizeTextField;
    }

    public void setFontSizeTextField(String fontSizeTextField) {
        this.fontSizeTextField.setText(fontSizeTextField);
    }

    public JComboBox getFontStyleComboBox() {
        return fontStyleComboBox;
    }

    public void setFontStyleComboBox(String fontStyleComboBox) {
        this.fontStyleComboBox.setSelectedItem(fontStyleComboBox);
    }

    public JTextField getFontWaterMarkTextField() {
        return fontWaterMarkTextField;
    }

    public void setFontWaterMarkTextField(String fontWaterMarkTextField) {
        this.fontWaterMarkTextField.setText(fontWaterMarkTextField);
    }

    public Color getInitFontColor() {
        return initFontColor;
    }

    public void setInitFontColor(Color initFontColor) {
        this.initFontColor = initFontColor;
    }

    public JCheckBox getAutoAddWaterMarkCheckBox() {
        return autoAddWaterMarkCheckBox;
    }

    public void setAutoAddWaterMarkCheckBox(Boolean autoAddWaterMarkCheckBox) {
        this.autoAddWaterMarkCheckBox.setSelected(autoAddWaterMarkCheckBox);
    }

    public JCheckBox getAutoFullScreenWatermarkCheckBox() {
        return autoFullScreenWatermarkCheckBox;
    }

    public void setAutoFullScreenWatermarkCheckBox(Boolean autoFullScreenWatermarkCheckBox) {
        this.autoFullScreenWatermarkCheckBox.setSelected(autoFullScreenWatermarkCheckBox);
    }

    public JSlider getFontWaterMarkTransparencySlider() {
        return fontWaterMarkTransparencySlider;
    }

    public void setFontWaterMarkTransparencySlider(Integer fontWaterMarkTransparencySlider) {
        this.fontWaterMarkTransparencySlider.setValue(fontWaterMarkTransparencySlider);
    }

    public JSlider getFontWaterMarkRotateSlider() {
        return fontWaterMarkRotateSlider;
    }

    public void setFontWaterMarkRotateSlider(Integer fontWaterMarkRotateSlider) {
        this.fontWaterMarkRotateSlider.setValue(fontWaterMarkRotateSlider);
    }

    public JSlider getFontWaterMarkSparsitySlider() {
        return fontWaterMarkSparsitySlider;
    }

    public void setFontWaterMarkSparsitySlider(Integer fontWaterMarkSparsitySlider) {
        this.fontWaterMarkSparsitySlider.setValue(fontWaterMarkSparsitySlider);
    }

}

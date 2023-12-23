package easy.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.JBColor;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.util.EasyCommonUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @project: EasyTool
 * @package: easy.form
 * @author: mabin
 * @date: 2023/12/17 11:01:56
 */
public class CommonSettingView {

    private static final Logger log = Logger.getInstance(CommonSettingView.class);

    private CommonConfig commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();

    private JPanel panel;
    private JPanel commonPanel;
    private JLabel swaggerConfirmModelTipsLabel;
    private JLabel swaggerConfirmModelLabel;
    private JCheckBox swaggerConfirmYesCheckBox;
    private JCheckBox swaggerConfirmNoCheckBox;
    private JLabel searchApiTipsLabel;
    private JLabel searchApiLabel;
    private JRadioButton searchApiDefaultIconRadioButton;
    private JRadioButton searchApiCuteIconRadioButton;
    private JLabel translateConfirmInputModelTipsLabel;
    private JLabel translateConfirmInputModelLabel;
    private JCheckBox translateConfirmInputModelYesCheckBox;
    private JCheckBox translateConfirmInputModelNoCheckBox;
    private JCheckBox tabHighlightEnableCheckBox;
    private JLabel tabBackgroundTipsLabel;
    private JLabel tabBackgroundColorLabel;
    private ColorPanel tabBackgroundColorPanel;
    private JLabel tabSettingGroupLabel;
    private JLabel tabHighlightSizeLabel;
    private JLabel tabHighlightSizeTipsLabel;
    private JComboBox tabHighlightSizeComboBox;
    private JLabel tabHighlightGradientStepLabel;
    private JLabel tabHighlightGradientStepTipsLabel;
    private JFormattedTextField tabHighlightGradientStepFormattedTextField;

    public CommonSettingView() {
        swaggerConfirmYesCheckBox.addChangeListener(e -> swaggerConfirmNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        swaggerConfirmNoCheckBox.addChangeListener(e -> swaggerConfirmYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        searchApiDefaultIconRadioButton.addChangeListener(e -> searchApiCuteIconRadioButton.setSelected(!((JRadioButton) e.getSource()).isSelected()));
        searchApiCuteIconRadioButton.addChangeListener(e -> searchApiDefaultIconRadioButton.setSelected(!((JRadioButton) e.getSource()).isSelected()));
        translateConfirmInputModelYesCheckBox.addChangeListener(e -> translateConfirmInputModelNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        translateConfirmInputModelNoCheckBox.addChangeListener(e -> translateConfirmInputModelYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        tabHighlightEnableCheckBox.addChangeListener(e -> {
            boolean selected = ((JCheckBox) e.getSource()).isSelected();
            tabBackgroundColorPanel.setEnabled(selected);
            tabHighlightSizeComboBox.setEnabled(selected);
            tabHighlightGradientStepFormattedTextField.setEnabled(selected);
        });
        // tips监听处理
        EasyCommonUtil.tipLabelMouseListener(swaggerConfirmModelTipsLabel, "swagger.confirm.model.checkBox.tip.text");
        EasyCommonUtil.tipLabelMouseListener(searchApiTipsLabel, "search.api.icon.tip.text");
        EasyCommonUtil.tipLabelMouseListener(translateConfirmInputModelTipsLabel, "translate.confirm.model.checkBox.tip.text");
        EasyCommonUtil.tipLabelMouseListener(tabBackgroundTipsLabel, "tab.background.checkBox.tip.text");
        EasyCommonUtil.tipLabelMouseListener(tabHighlightSizeTipsLabel, "tab.highlight.size.tip.text");
        EasyCommonUtil.tipLabelMouseListener(tabHighlightGradientStepTipsLabel, "tab.highlight.gradient.step.tip.text");
    }

    private void createUIComponents() {
        commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();
    }

    /**
     * 通用设置重置处理
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/17 11:47
     */
    public void reset() {
        if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmYesCheckBox())) {
            setSwaggerConfirmYesCheckBox(Boolean.TRUE);
            setSwaggerConfirmNoCheckBox(Boolean.FALSE);
        } else {
            setSwaggerConfirmYesCheckBox(Boolean.FALSE);
            setSwaggerConfirmNoCheckBox(Boolean.TRUE);
        }
        if (Boolean.TRUE.equals(commonConfig.getSearchApiDefaultIconRadioButton())) {
            setSearchApiDefaultIconRadioButton(Boolean.TRUE);
            setSearchApiCuteIconRadioButton(Boolean.FALSE);
        } else {
            setSearchApiDefaultIconRadioButton(Boolean.FALSE);
            setSearchApiCuteIconRadioButton(Boolean.TRUE);
        }
        if (Boolean.TRUE.equals(commonConfig.getTranslateConfirmInputModelYesCheckBox())) {
            setTranslateConfirmInputModelYesCheckBox(Boolean.TRUE);
            setTranslateConfirmInputModelNoCheckBox(Boolean.FALSE);
        } else {
            setTranslateConfirmInputModelYesCheckBox(Boolean.FALSE);
            setTranslateConfirmInputModelNoCheckBox(Boolean.TRUE);
        }
        setTabHighlightEnableCheckBox(commonConfig.getTabHighlightEnableCheckBox());
        CommonConfig.PersistentColor persistentColor = commonConfig.getPersistentColor();
        setTabBackgroundColorPanel(Objects.isNull(persistentColor) ? new JBColor(Color.MAGENTA, new Color(174, 80, 250)) : persistentColor.getColor());
        setTabHighlightSizeComboBox(commonConfig.getTabHighlightSizeComboBox());
        setTabHighlightGradientStepFormattedTextField(commonConfig.getTabHighlightGradientStepFormattedTextField());
    }

    public JComponent getComponent() {
        return panel;
    }

    public JPanel getCommonPanel() {
        return commonPanel;
    }

    public void setCommonPanel(JPanel commonPanel) {
        this.commonPanel = commonPanel;
    }

    public JLabel getSwaggerConfirmModelTipsLabel() {
        return swaggerConfirmModelTipsLabel;
    }

    public void setSwaggerConfirmModelTipsLabel(JLabel swaggerConfirmModelTipsLabel) {
        this.swaggerConfirmModelTipsLabel = swaggerConfirmModelTipsLabel;
    }

    public JLabel getSwaggerConfirmModelLabel() {
        return swaggerConfirmModelLabel;
    }

    public void setSwaggerConfirmModelLabel(JLabel swaggerConfirmModelLabel) {
        this.swaggerConfirmModelLabel = swaggerConfirmModelLabel;
    }

    public JCheckBox getSwaggerConfirmYesCheckBox() {
        return swaggerConfirmYesCheckBox;
    }

    public void setSwaggerConfirmYesCheckBox(Boolean swaggerConfirmYesCheckBox) {
        this.swaggerConfirmYesCheckBox.setSelected(swaggerConfirmYesCheckBox);
    }

    public JCheckBox getSwaggerConfirmNoCheckBox() {
        return swaggerConfirmNoCheckBox;
    }

    public void setSwaggerConfirmNoCheckBox(Boolean swaggerConfirmNoCheckBox) {
        this.swaggerConfirmNoCheckBox.setSelected(swaggerConfirmNoCheckBox);
    }

    public JLabel getSearchApiTipsLabel() {
        return searchApiTipsLabel;
    }

    public void setSearchApiTipsLabel(JLabel searchApiTipsLabel) {
        this.searchApiTipsLabel = searchApiTipsLabel;
    }

    public JLabel getSearchApiLabel() {
        return searchApiLabel;
    }

    public void setSearchApiLabel(JLabel searchApiLabel) {
        this.searchApiLabel = searchApiLabel;
    }

    public JRadioButton getSearchApiDefaultIconRadioButton() {
        return searchApiDefaultIconRadioButton;
    }

    public void setSearchApiDefaultIconRadioButton(Boolean searchApiDefaultIconRadioButton) {
        this.searchApiDefaultIconRadioButton.setSelected(searchApiDefaultIconRadioButton);
    }

    public JRadioButton getSearchApiCuteIconRadioButton() {
        return searchApiCuteIconRadioButton;
    }

    public void setSearchApiCuteIconRadioButton(Boolean searchApiCuteIconRadioButton) {
        this.searchApiCuteIconRadioButton.setSelected(searchApiCuteIconRadioButton);
    }

    public JLabel getTranslateConfirmInputModelTipsLabel() {
        return translateConfirmInputModelTipsLabel;
    }

    public void setTranslateConfirmInputModelTipsLabel(JLabel translateConfirmInputModelTipsLabel) {
        this.translateConfirmInputModelTipsLabel = translateConfirmInputModelTipsLabel;
    }

    public JLabel getTranslateConfirmInputModelLabel() {
        return translateConfirmInputModelLabel;
    }

    public void setTranslateConfirmInputModelLabel(JLabel translateConfirmInputModelLabel) {
        this.translateConfirmInputModelLabel = translateConfirmInputModelLabel;
    }

    public JCheckBox getTranslateConfirmInputModelYesCheckBox() {
        return translateConfirmInputModelYesCheckBox;
    }

    public void setTranslateConfirmInputModelYesCheckBox(Boolean translateConfirmInputModelYesCheckBox) {
        this.translateConfirmInputModelYesCheckBox.setSelected(translateConfirmInputModelYesCheckBox);
    }

    public JCheckBox getTranslateConfirmInputModelNoCheckBox() {
        return translateConfirmInputModelNoCheckBox;
    }

    public void setTranslateConfirmInputModelNoCheckBox(Boolean translateConfirmInputModelNoCheckBox) {
        this.translateConfirmInputModelNoCheckBox.setSelected(translateConfirmInputModelNoCheckBox);
    }

    public JLabel getTabBackgroundColorLabel() {
        return tabBackgroundColorLabel;
    }

    public void setTabBackgroundColorLabel(JLabel tabBackgroundColorLabel) {
        this.tabBackgroundColorLabel = tabBackgroundColorLabel;
    }

    public ColorPanel getTabBackgroundColorPanel() {
        return tabBackgroundColorPanel;
    }

    public void setTabBackgroundColorPanel(Color tabBackgroundColorPanel) {
        this.tabBackgroundColorPanel.setSelectedColor(tabBackgroundColorPanel);
    }

    public JLabel getTabSettingGroupLabel() {
        return tabSettingGroupLabel;
    }

    public void setTabSettingGroupLabel(JLabel tabSettingGroupLabel) {
        this.tabSettingGroupLabel = tabSettingGroupLabel;
    }

    public JLabel getTabHighlightSizeLabel() {
        return tabHighlightSizeLabel;
    }

    public void setTabHighlightSizeLabel(JLabel tabHighlightSizeLabel) {
        this.tabHighlightSizeLabel = tabHighlightSizeLabel;
    }

    public JLabel getTabHighlightSizeTipsLabel() {
        return tabHighlightSizeTipsLabel;
    }

    public void setTabHighlightSizeTipsLabel(JLabel tabHighlightSizeTipsLabel) {
        this.tabHighlightSizeTipsLabel = tabHighlightSizeTipsLabel;
    }

    public JComboBox getTabHighlightSizeComboBox() {
        return tabHighlightSizeComboBox;
    }

    public void setTabHighlightSizeComboBox(String tabHighlightSizeComboBox) {
        this.tabHighlightSizeComboBox.setSelectedItem(tabHighlightSizeComboBox);
    }

    public JLabel getTabHighlightGradientStepLabel() {
        return tabHighlightGradientStepLabel;
    }

    public void setTabHighlightGradientStepLabel(JLabel tabHighlightGradientStepLabel) {
        this.tabHighlightGradientStepLabel = tabHighlightGradientStepLabel;
    }

    public JLabel getTabHighlightGradientStepTipsLabel() {
        return tabHighlightGradientStepTipsLabel;
    }

    public void setTabHighlightGradientStepTipsLabel(JLabel tabHighlightGradientStepTipsLabel) {
        this.tabHighlightGradientStepTipsLabel = tabHighlightGradientStepTipsLabel;
    }

    public JFormattedTextField getTabHighlightGradientStepFormattedTextField() {
        return tabHighlightGradientStepFormattedTextField;
    }

    public void setTabHighlightGradientStepFormattedTextField(String tabHighlightGradientStepFormattedTextField) {
        this.tabHighlightGradientStepFormattedTextField.setText(tabHighlightGradientStepFormattedTextField);
    }

    public JCheckBox getTabHighlightEnableCheckBox() {
        return tabHighlightEnableCheckBox;
    }

    public void setTabHighlightEnableCheckBox(Boolean tabHighlightEnableCheckBox) {
        this.tabHighlightEnableCheckBox.setSelected(tabHighlightEnableCheckBox);
    }

}

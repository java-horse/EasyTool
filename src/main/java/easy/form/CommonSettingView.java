package easy.form;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.JBColor;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.util.BundleUtil;

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
    private JLabel swaggerGroupLabel;
    private JLabel swaggerConfirmModelTipsLabel;
    private JLabel swaggerConfirmModelLabel;
    private JCheckBox swaggerConfirmYesCheckBox;
    private JCheckBox swaggerConfirmNoCheckBox;
    private JLabel restfulApiGroupLabel;
    private JLabel searchApiTipsLabel;
    private JLabel searchApiLabel;
    private JRadioButton searchApiDefaultIconRadioButton;
    private JRadioButton searchApiCuteIconRadioButton;
    private JLabel translateGroupLabel;
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
    private JLabel convertCharGroupLabel;
    private JCheckBox convertCharEnableCheckBox;

    public CommonSettingView() {
        // 设置提示小图标和提示信息
        setTipsLabel();
        // 设置监听器
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
    }

    private void createUIComponents() {
        commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();
    }

    /**
     * 设置提示小图标和提示信息
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/27 15:43
     */
    private void setTipsLabel() {
        swaggerConfirmModelTipsLabel.setIcon(AllIcons.General.ContextHelp);
        searchApiTipsLabel.setIcon(AllIcons.General.ContextHelp);
        translateConfirmInputModelTipsLabel.setIcon(AllIcons.General.ContextHelp);
        tabBackgroundTipsLabel.setIcon(AllIcons.General.ContextHelp);
        tabHighlightSizeTipsLabel.setIcon(AllIcons.General.ContextHelp);
        tabHighlightGradientStepTipsLabel.setIcon(AllIcons.General.ContextHelp);

        swaggerConfirmModelTipsLabel.setToolTipText(BundleUtil.getI18n("swagger.confirm.model.checkBox.tip.text"));
        searchApiTipsLabel.setToolTipText(BundleUtil.getI18n("search.api.icon.tip.text"));
        translateConfirmInputModelTipsLabel.setToolTipText(BundleUtil.getI18n("translate.confirm.model.checkBox.tip.text"));
        tabBackgroundTipsLabel.setToolTipText(BundleUtil.getI18n("tab.background.checkBox.tip.text"));
        tabHighlightSizeTipsLabel.setToolTipText(BundleUtil.getI18n("tab.highlight.size.tip.text"));
        tabHighlightGradientStepTipsLabel.setToolTipText(BundleUtil.getI18n("tab.highlight.gradient.step.tip.text"));
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
        setConvertCharEnableCheckBox(commonConfig.getConvertCharEnableCheckBox());
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

    public JLabel getConvertCharGroupLabel() {
        return convertCharGroupLabel;
    }

    public void setConvertCharGroupLabel(JLabel convertCharGroupLabel) {
        this.convertCharGroupLabel = convertCharGroupLabel;
    }

    public JCheckBox getConvertCharEnableCheckBox() {
        return convertCharEnableCheckBox;
    }

    public void setConvertCharEnableCheckBox(Boolean convertCharEnableCheckBox) {
        this.convertCharEnableCheckBox.setSelected(convertCharEnableCheckBox);
    }

    public JLabel getSwaggerGroupLabel() {
        return swaggerGroupLabel;
    }

    public void setSwaggerGroupLabel(JLabel swaggerGroupLabel) {
        this.swaggerGroupLabel = swaggerGroupLabel;
    }

    public JLabel getRestfulApiGroupLabel() {
        return restfulApiGroupLabel;
    }

    public void setRestfulApiGroupLabel(JLabel restfulApiGroupLabel) {
        this.restfulApiGroupLabel = restfulApiGroupLabel;
    }

    public JLabel getTranslateGroupLabel() {
        return translateGroupLabel;
    }

    public void setTranslateGroupLabel(JLabel translateGroupLabel) {
        this.translateGroupLabel = translateGroupLabel;
    }

}

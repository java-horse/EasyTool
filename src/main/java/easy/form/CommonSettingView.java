package easy.form;

import com.intellij.ui.ColorPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.OnOffButton;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import easy.icons.EasyIcons;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @project: EasyTool
 * @package: easy.form
 * @author: mabin
 * @date: 2023/12/17 11:01:56
 */
public class CommonSettingView {

    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

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
    private JLabel restfulDisplayApiCommentLabel;
    private JCheckBox restfulDisplayApiCommentCheckBox;
    private JLabel restfulDisplayApiCommentTipLabel;
    private JLabel pluginUpdateGroupLabel;
    private JLabel pluginAutoUpdateEnableLabel;
    private OnOffButton pluginAutoUpdateEnableButton;
    private JCheckBox swaggerHintCheckBox;
    private JLabel restfulDisplayApiPreviewLabel;

    public CommonSettingView() {
        // 设置提示小图标和提示信息
        setTipsLabel();
        // 设置监听器
        swaggerConfirmYesCheckBox.addChangeListener(e -> swaggerConfirmNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        swaggerConfirmNoCheckBox.addChangeListener(e -> swaggerConfirmYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        searchApiDefaultIconRadioButton.addChangeListener(e -> {
            boolean selected = ((JRadioButton) e.getSource()).isSelected();
            searchApiCuteIconRadioButton.setSelected(!selected);
            if (selected) {
                restfulDisplayApiPreviewLabel.setIcon(EasyIcons.ICON.CUTE_GET);
            }
        });
        searchApiCuteIconRadioButton.addChangeListener(e -> {
            boolean selected = ((JRadioButton) e.getSource()).isSelected();
            searchApiDefaultIconRadioButton.setSelected(!selected);
            if (selected) {
                restfulDisplayApiPreviewLabel.setIcon(EasyIcons.ICON.DEFAULT_GET);
            }
        });
        translateConfirmInputModelYesCheckBox.addChangeListener(e -> translateConfirmInputModelNoCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        translateConfirmInputModelNoCheckBox.addChangeListener(e -> translateConfirmInputModelYesCheckBox.setSelected(!((JCheckBox) e.getSource()).isSelected()));
        tabBackgroundColorPanel.setEnabled(false);
        tabHighlightSizeComboBox.setEnabled(false);
        tabHighlightGradientStepFormattedTextField.setEnabled(false);
        tabHighlightEnableCheckBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            tabBackgroundColorPanel.setEnabled(selected);
            tabHighlightSizeComboBox.setEnabled(selected);
            tabHighlightGradientStepFormattedTextField.setEnabled(selected);
        });
        tabHighlightEnableCheckBox.setSelected(commonConfig.getTabHighlightEnableCheckBox());
        pluginAutoUpdateEnableButton.addActionListener(e -> {
            if (pluginAutoUpdateEnableButton.isSelected()) {
                MessageUtil.showWarningDialog(String.format("插件【%s】自动更新属于实验性功能, 存在不稳定性, 建议在IDE插件面板更新", Constants.PLUGIN_NAME));
            }
        });
    }

    private void createUIComponents() {
        commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();
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
        EasyCommonUtil.customLabelTipText(swaggerConfirmModelTipsLabel, BundleUtil.getI18n("swagger.confirm.model.checkBox.tip.text"));
        EasyCommonUtil.customLabelTipText(searchApiTipsLabel, BundleUtil.getI18n("search.api.icon.tip.text"));
        EasyCommonUtil.customLabelTipText(translateConfirmInputModelTipsLabel, BundleUtil.getI18n("translate.confirm.model.checkBox.tip.text"));
        EasyCommonUtil.customLabelTipText(tabBackgroundTipsLabel, BundleUtil.getI18n("tab.background.checkBox.tip.text"));
        EasyCommonUtil.customLabelTipText(tabHighlightSizeTipsLabel, BundleUtil.getI18n("tab.highlight.size.tip.text"));
        EasyCommonUtil.customLabelTipText(tabHighlightGradientStepTipsLabel, BundleUtil.getI18n("tab.highlight.gradient.step.tip.text"));
        EasyCommonUtil.customLabelTipText(restfulDisplayApiCommentTipLabel, "开启后会尝试获取API的JavaDoc和Swagger的说明文本并展示");
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
        setTabBackgroundColorPanel(new JBColor(new Color(commonConfig.getTabHighlightBackgroundColor(), true), new Color(commonConfig.getTabHighlightBackgroundColor(), true)));
        setTabHighlightSizeComboBox(commonConfig.getTabHighlightSizeComboBox());
        setTabHighlightGradientStepFormattedTextField(commonConfig.getTabHighlightGradientStepFormattedTextField());
        setConvertCharEnableCheckBox(commonConfig.getConvertCharEnableCheckBox());
        setRestfulDisplayApiCommentCheckBox(commonConfig.getRestfulDisplayApiCommentCheckBox());
        setPluginAutoUpdateEnableButton(commonConfig.getPluginAutoUpdateEnable());
        setSwaggerHintCheckBox(commonConfig.getSwaggerHintCheckBox());
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

    public JLabel getRestfulDisplayApiCommentLabel() {
        return restfulDisplayApiCommentLabel;
    }

    public void setRestfulDisplayApiCommentLabel(JLabel restfulDisplayApiCommentLabel) {
        this.restfulDisplayApiCommentLabel = restfulDisplayApiCommentLabel;
    }

    public JCheckBox getRestfulDisplayApiCommentCheckBox() {
        return restfulDisplayApiCommentCheckBox;
    }

    public void setRestfulDisplayApiCommentCheckBox(Boolean restfulDisplayApiCommentCheckBox) {
        this.restfulDisplayApiCommentCheckBox.setSelected(restfulDisplayApiCommentCheckBox);
    }

    public JLabel getRestfulDisplayApiCommentTipLabel() {
        return restfulDisplayApiCommentTipLabel;
    }

    public void setRestfulDisplayApiCommentTipLabel(JLabel restfulDisplayApiCommentTipLabel) {
        this.restfulDisplayApiCommentTipLabel = restfulDisplayApiCommentTipLabel;
    }

    public OnOffButton getPluginAutoUpdateEnableButton() {
        return pluginAutoUpdateEnableButton;
    }

    public void setPluginAutoUpdateEnableButton(Boolean pluginAutoUpdateEnableButton) {
        this.pluginAutoUpdateEnableButton.setSelected(pluginAutoUpdateEnableButton);
    }

    public JCheckBox getSwaggerHintCheckBox() {
        return swaggerHintCheckBox;
    }

    public void setSwaggerHintCheckBox(Boolean swaggerHintCheckBox) {
        this.swaggerHintCheckBox.setSelected(swaggerHintCheckBox);
    }

}

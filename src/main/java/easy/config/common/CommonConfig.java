package easy.config.common;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

/**
 * 通用设置
 *
 * @project: EasyTool
 * @package: easy.config.common
 * @author: mabin
 * @date: 2023/12/17 11:29:57
 */
public class CommonConfig {

    private Boolean swaggerConfirmYesCheckBox;
    private Boolean swaggerConfirmNoCheckBox;
    private Boolean searchApiDefaultIconRadioButton;
    private Boolean searchApiCuteIconRadioButton;
    private Boolean restfulDisplayApiCommentCheckBox;
    private Boolean translateConfirmInputModelYesCheckBox;
    private Boolean translateConfirmInputModelNoCheckBox;
    private String tabHighlightSizeComboBox;
    private String tabHighlightGradientStepFormattedTextField;
    private Boolean tabHighlightEnableCheckBox;
    private Integer tabHighlightBackgroundColor;
    private Boolean convertCharEnableCheckBox;
    private Boolean pluginAutoUpdateEnable;
    private Boolean swaggerHintCheckBox;

    public Boolean getSwaggerConfirmYesCheckBox() {
        return swaggerConfirmYesCheckBox;
    }

    public void setSwaggerConfirmYesCheckBox(Boolean swaggerConfirmYesCheckBox) {
        this.swaggerConfirmYesCheckBox = swaggerConfirmYesCheckBox;
    }

    public Boolean getSwaggerConfirmNoCheckBox() {
        return swaggerConfirmNoCheckBox;
    }

    public void setSwaggerConfirmNoCheckBox(Boolean swaggerConfirmNoCheckBox) {
        this.swaggerConfirmNoCheckBox = swaggerConfirmNoCheckBox;
    }

    public Boolean getSearchApiDefaultIconRadioButton() {
        return searchApiDefaultIconRadioButton;
    }

    public void setSearchApiDefaultIconRadioButton(Boolean searchApiDefaultIconRadioButton) {
        this.searchApiDefaultIconRadioButton = searchApiDefaultIconRadioButton;
    }

    public Boolean getSearchApiCuteIconRadioButton() {
        return searchApiCuteIconRadioButton;
    }

    public void setSearchApiCuteIconRadioButton(Boolean searchApiCuteIconRadioButton) {
        this.searchApiCuteIconRadioButton = searchApiCuteIconRadioButton;
    }

    public Boolean getTranslateConfirmInputModelYesCheckBox() {
        return translateConfirmInputModelYesCheckBox;
    }

    public void setTranslateConfirmInputModelYesCheckBox(Boolean translateConfirmInputModelYesCheckBox) {
        this.translateConfirmInputModelYesCheckBox = translateConfirmInputModelYesCheckBox;
    }

    public Boolean getTranslateConfirmInputModelNoCheckBox() {
        return translateConfirmInputModelNoCheckBox;
    }

    public void setTranslateConfirmInputModelNoCheckBox(Boolean translateConfirmInputModelNoCheckBox) {
        this.translateConfirmInputModelNoCheckBox = translateConfirmInputModelNoCheckBox;
    }

    public Integer getTabHighlightBackgroundColor() {
        return tabHighlightBackgroundColor;
    }

    public void setTabHighlightBackgroundColor(Integer tabHighlightBackgroundColor) {
        this.tabHighlightBackgroundColor = tabHighlightBackgroundColor;
    }

    public String getTabHighlightSizeComboBox() {
        return tabHighlightSizeComboBox;
    }

    public void setTabHighlightSizeComboBox(String tabHighlightSizeComboBox) {
        this.tabHighlightSizeComboBox = tabHighlightSizeComboBox;
    }

    public String getTabHighlightGradientStepFormattedTextField() {
        return tabHighlightGradientStepFormattedTextField;
    }

    public void setTabHighlightGradientStepFormattedTextField(String tabHighlightGradientStepFormattedTextField) {
        this.tabHighlightGradientStepFormattedTextField = tabHighlightGradientStepFormattedTextField;
    }

    public Boolean getTabHighlightEnableCheckBox() {
        return tabHighlightEnableCheckBox;
    }

    public void setTabHighlightEnableCheckBox(Boolean tabHighlightEnableCheckBox) {
        this.tabHighlightEnableCheckBox = tabHighlightEnableCheckBox;
    }

    public Boolean getConvertCharEnableCheckBox() {
        return convertCharEnableCheckBox;
    }

    public void setConvertCharEnableCheckBox(Boolean convertCharEnableCheckBox) {
        this.convertCharEnableCheckBox = convertCharEnableCheckBox;
    }

    public Boolean getRestfulDisplayApiCommentCheckBox() {
        return restfulDisplayApiCommentCheckBox;
    }

    public void setRestfulDisplayApiCommentCheckBox(Boolean restfulDisplayApiCommentCheckBox) {
        this.restfulDisplayApiCommentCheckBox = restfulDisplayApiCommentCheckBox;
    }

    public Boolean getPluginAutoUpdateEnable() {
        return pluginAutoUpdateEnable;
    }

    public void setPluginAutoUpdateEnable(Boolean pluginAutoUpdateEnable) {
        this.pluginAutoUpdateEnable = pluginAutoUpdateEnable;
    }

    public Boolean getSwaggerHintCheckBox() {
        return swaggerHintCheckBox;
    }

    public void setSwaggerHintCheckBox(Boolean swaggerHintCheckBox) {
        this.swaggerHintCheckBox = swaggerHintCheckBox;
    }

}

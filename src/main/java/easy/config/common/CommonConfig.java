package easy.config.common;

import com.intellij.ui.JBColor;

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
    private PersistentColor persistentColor;
    private String tabHighlightSizeComboBox;
    private String tabHighlightGradientStepFormattedTextField;
    private Boolean tabHighlightEnableCheckBox;
    private Boolean convertCharEnableCheckBox;
    private Boolean pluginAutoUpdateEnable;

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

    public PersistentColor getPersistentColor() {
        return persistentColor;
    }

    public void setPersistentColor(PersistentColor persistentColor) {
        this.persistentColor = persistentColor;
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

    /**
     * tab标签颜色持久化
     *
     * @author mabin
     * @return
     * @date 2023/12/20 14:41
     */
    public static class PersistentColor {
        private Integer red;
        private Integer green;
        private Integer blue;

        public Color getColor() {
            return new JBColor(Color.MAGENTA, new Color(red, green, blue));
        }

        public Integer getRed() {
            return red;
        }

        public void setRed(Integer red) {
            this.red = red;
        }

        public Integer getGreen() {
            return green;
        }

        public void setGreen(Integer green) {
            this.green = green;
        }

        public Integer getBlue() {
            return blue;
        }

        public void setBlue(Integer blue) {
            this.blue = blue;
        }

        @Override
        public String toString() {
            return "PersistentColor{" +
                    "red=" + red +
                    ", green=" + green +
                    ", blue=" + blue +
                    '}';
        }
    }

}

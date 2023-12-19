package easy.config.common;

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
    private Boolean translateConfirmInputModelYesCheckBox;
    private Boolean translateConfirmInputModelNoCheckBox;


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

    @Override
    public String toString() {
        return "CommonConfig{" +
                "swaggerConfirmYesCheckBox=" + swaggerConfirmYesCheckBox +
                ", swaggerConfirmNoCheckBox=" + swaggerConfirmNoCheckBox +
                ", searchApiDefaultIconRadioButton=" + searchApiDefaultIconRadioButton +
                ", searchApiCuteIconRadioButton=" + searchApiCuteIconRadioButton +
                ", translateConfirmInputModelYesCheckBox=" + translateConfirmInputModelYesCheckBox +
                ", translateConfirmInputModelNoCheckBox=" + translateConfirmInputModelNoCheckBox +
                '}';
    }

}

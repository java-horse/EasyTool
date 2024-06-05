package easy.config.screenshot;

/**
 * 代码截图配置
 *
 * @author mabin
 * @project EasyTool
 * @package easy.config.screenshot
 * @date 2024/06/03 14:34
 */
public class CodeScreenshotConfig {
    private Double scale;
    private Double innerPadding;
    private Double outerPadding;
    private Integer windowRoundness;
    private Integer backgroundColor;
    private Boolean removeIndentation;
    private Boolean showWindowIcons;
    private Boolean autoCopyPayboard;
    private String customFileName;
    private String customFileNameFormat;
    private String customFileNameSuffix;

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Double getInnerPadding() {
        return innerPadding;
    }

    public void setInnerPadding(Double innerPadding) {
        this.innerPadding = innerPadding;
    }

    public Double getOuterPadding() {
        return outerPadding;
    }

    public void setOuterPadding(Double outerPadding) {
        this.outerPadding = outerPadding;
    }

    public Integer getWindowRoundness() {
        return windowRoundness;
    }

    public void setWindowRoundness(Integer windowRoundness) {
        this.windowRoundness = windowRoundness;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Boolean getRemoveIndentation() {
        return removeIndentation;
    }

    public void setRemoveIndentation(Boolean removeIndentation) {
        this.removeIndentation = removeIndentation;
    }

    public Boolean getShowWindowIcons() {
        return showWindowIcons;
    }

    public void setShowWindowIcons(Boolean showWindowIcons) {
        this.showWindowIcons = showWindowIcons;
    }

    public Boolean getAutoCopyPayboard() {
        return autoCopyPayboard;
    }

    public void setAutoCopyPayboard(Boolean autoCopyPayboard) {
        this.autoCopyPayboard = autoCopyPayboard;
    }

    public String getCustomFileName() {
        return customFileName;
    }

    public void setCustomFileName(String customFileName) {
        this.customFileName = customFileName;
    }

    public String getCustomFileNameFormat() {
        return customFileNameFormat;
    }

    public void setCustomFileNameFormat(String customFileNameFormat) {
        this.customFileNameFormat = customFileNameFormat;
    }

    public String getCustomFileNameSuffix() {
        return customFileNameSuffix;
    }

    public void setCustomFileNameSuffix(String customFileNameSuffix) {
        this.customFileNameSuffix = customFileNameSuffix;
    }

}

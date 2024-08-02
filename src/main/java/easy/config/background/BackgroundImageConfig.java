package easy.config.background;

/**
 * 背景图像配置
 *
 * @author mabin
 * @project EasyTool
 * @package easy.config.background
 * @date 2024/08/01 18:09
 */
public class BackgroundImageConfig {
    private Boolean imageSwitch;
    private Integer imageCount;
    private Integer imageTimeModel;
    private String imageTimeUnit;
    private String imageScope;
    private String imageFilePath;

    public Boolean getImageSwitch() {
        return imageSwitch;
    }

    public void setImageSwitch(Boolean imageSwitch) {
        this.imageSwitch = imageSwitch;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public Integer getImageTimeModel() {
        return imageTimeModel;
    }

    public void setImageTimeModel(Integer imageTimeModel) {
        this.imageTimeModel = imageTimeModel;
    }

    public String getImageTimeUnit() {
        return imageTimeUnit;
    }

    public void setImageTimeUnit(String imageTimeUnit) {
        this.imageTimeUnit = imageTimeUnit;
    }

    public String getImageScope() {
        return imageScope;
    }

    public void setImageScope(String imageScope) {
        this.imageScope = imageScope;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }
}

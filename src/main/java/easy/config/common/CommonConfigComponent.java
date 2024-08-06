package easy.config.common;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 通用设置数据持久化
 *
 * @project: EasyTool
 * @package: easy.config.common
 * @author: mabin
 * @date: 2023/12/17 11:30:35
 */
@State(name = Constants.PLUGIN_NAME + "CommonConfig", storages = {@Storage(Constants.PLUGIN_NAME + "CommonConfig.xml")})
public class CommonConfigComponent implements PersistentStateComponent<CommonConfig> {

    private CommonConfig commonConfig;

    @Override
    public @Nullable CommonConfig getState() {
        if (Objects.isNull(commonConfig)) {
            commonConfig = new CommonConfig();
            commonConfig.setSwaggerConfirmYesCheckBox(Boolean.TRUE);
            commonConfig.setSearchApiDefaultIconRadioButton(Boolean.TRUE);
            commonConfig.setRestfulDisplayApiCommentCheckBox(Boolean.FALSE);
            commonConfig.setTranslateConfirmInputModelYesCheckBox(Boolean.TRUE);
            commonConfig.setTabHighlightEnableCheckBox(Boolean.FALSE);
            commonConfig.setTabHighlightBackgroundColor(0xff7857be);
            commonConfig.setTabHighlightSizeComboBox(Integer.toString(Constants.NUM.ONE));
            commonConfig.setTabHighlightGradientStepFormattedTextField(Integer.toString(Constants.NUM.TEN));
            commonConfig.setConvertCharEnableCheckBox(Boolean.TRUE);
            commonConfig.setPluginAutoUpdateEnable(Boolean.FALSE);
            commonConfig.setSwaggerHintCheckBox(Boolean.FALSE);
        } else {
            commonConfig.setSwaggerConfirmYesCheckBox(Objects.isNull(commonConfig.getSwaggerConfirmYesCheckBox()) ? Boolean.TRUE : commonConfig.getSwaggerConfirmYesCheckBox());
            commonConfig.setSearchApiDefaultIconRadioButton(Objects.isNull(commonConfig.getSearchApiDefaultIconRadioButton()) ? Boolean.TRUE : commonConfig.getSearchApiDefaultIconRadioButton());
            commonConfig.setRestfulDisplayApiCommentCheckBox(Objects.isNull(commonConfig.getRestfulDisplayApiCommentCheckBox()) ? Boolean.FALSE : commonConfig.getRestfulDisplayApiCommentCheckBox());
            commonConfig.setTranslateConfirmInputModelYesCheckBox(Objects.isNull(commonConfig.getTranslateConfirmInputModelYesCheckBox()) ? Boolean.TRUE : commonConfig.getTranslateConfirmInputModelYesCheckBox());
            commonConfig.setTabHighlightEnableCheckBox(Objects.isNull(commonConfig.getTabHighlightEnableCheckBox()) ? Boolean.FALSE : commonConfig.getTabHighlightEnableCheckBox());
            commonConfig.setTabHighlightBackgroundColor(Objects.isNull(commonConfig.getTabHighlightBackgroundColor()) ? 0xff7857be : commonConfig.getTabHighlightBackgroundColor());
            commonConfig.setTabHighlightSizeComboBox(Objects.isNull(commonConfig.getTabHighlightSizeComboBox()) ? Integer.toString(Constants.NUM.ONE) : commonConfig.getTabHighlightSizeComboBox());
            commonConfig.setTabHighlightGradientStepFormattedTextField(Objects.isNull(commonConfig.getTabHighlightGradientStepFormattedTextField()) ? Integer.toString(Constants.NUM.TEN) : commonConfig.getTabHighlightGradientStepFormattedTextField());
            commonConfig.setConvertCharEnableCheckBox(Objects.isNull(commonConfig.getConvertCharEnableCheckBox()) ? Boolean.TRUE : commonConfig.getConvertCharEnableCheckBox());
            commonConfig.setPluginAutoUpdateEnable(Objects.isNull(commonConfig.getPluginAutoUpdateEnable()) ? Boolean.FALSE : commonConfig.getPluginAutoUpdateEnable());
            commonConfig.setSwaggerHintCheckBox(Objects.isNull(commonConfig.getSwaggerHintCheckBox()) ? Boolean.FALSE : commonConfig.getSwaggerHintCheckBox());
        }
        return commonConfig;
    }

    @Override
    public void loadState(@NotNull CommonConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}

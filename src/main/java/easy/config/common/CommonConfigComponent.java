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
        }
        return commonConfig;
    }

    @Override
    public void loadState(@NotNull CommonConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}

package easy.config.background;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import easy.enums.BackgroundImageChangeScopeEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 背景图像配置组件
 *
 * @author mabin
 * @project EasyTool
 * @package easy.config.background
 * @date 2024/08/02 09:28
 */
@State(name = Constants.PLUGIN_NAME + "BackgroundImageConfig", storages = {@Storage(Constants.PLUGIN_NAME + "BackgroundImageConfig.xml")})
public class BackgroundImageConfigComponent implements PersistentStateComponent<BackgroundImageConfig> {

    private BackgroundImageConfig config;

    @Override
    public @Nullable BackgroundImageConfig getState() {
        if (Objects.isNull(config)) {
            config = new BackgroundImageConfig();
            config.setImageSwitch(Boolean.FALSE);
            config.setImageCount(Constants.NUM.TWO);
            config.setImageTimeModel(Constants.NUM.FIVE);
            config.setImageTimeUnit(TimeUnit.MINUTES.name());
            config.setImageScope(BackgroundImageChangeScopeEnum.BOTH.getName());
        } else {
            config.setImageSwitch(Objects.isNull(config.getImageSwitch()) ? Boolean.FALSE : config.getImageSwitch());
            config.setImageCount(Objects.isNull(config.getImageCount()) ? Constants.NUM.TWO : config.getImageCount());
            config.setImageTimeModel(Objects.isNull(config.getImageTimeModel()) ? Constants.NUM.FIVE : config.getImageTimeModel());
            config.setImageTimeUnit(Objects.isNull(config.getImageTimeUnit()) ? TimeUnit.MINUTES.name() : config.getImageTimeUnit());
            config.setImageScope(Objects.isNull(config.getImageScope()) ? BackgroundImageChangeScopeEnum.BOTH.getName() : config.getImageScope());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull BackgroundImageConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}

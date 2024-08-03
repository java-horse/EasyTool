package easy.config.convert;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import easy.form.convert.ConvertCharView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.TreeMap;

@State(name = Constants.PLUGIN_NAME + "ConvertCharConfig", storages = {@Storage(Constants.PLUGIN_NAME + "ConvertCharConfig.xml")})
public class ConvertCharConfigComponent implements PersistentStateComponent<ConvertCharConfig> {

    private ConvertCharConfig config;

    @Override
    public @Nullable ConvertCharConfig getState() {
        if (Objects.isNull(config)) {
            config = new ConvertCharConfig();
            config.setConvertCharMap(Constants.DEFAULT_CHAR_MAPPING);
        } else {
            config.setConvertCharMap(Objects.isNull(config.getConvertCharMap()) ? new TreeMap<>() : config.getConvertCharMap());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull ConvertCharConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }
}

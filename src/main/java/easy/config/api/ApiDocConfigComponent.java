package easy.config.api;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = Constants.PLUGIN_NAME + "ApiDocConfig", storages = {@Storage(Constants.PLUGIN_NAME + "ApiDocConfig.xml")})
public class ApiDocConfigComponent implements PersistentStateComponent<ApiDocConfig> {

    private ApiDocConfig apiDocConfig;

    @Override
    public @Nullable ApiDocConfig getState() {
        if (Objects.isNull(apiDocConfig)) {
            apiDocConfig = new ApiDocConfig();
            apiDocConfig.setYapiEnable(Boolean.FALSE);
            apiDocConfig.setApifoxEnable(Boolean.FALSE);
        } else {
            apiDocConfig.setYapiEnable(Objects.isNull(apiDocConfig.getYapiEnable()) ? Boolean.FALSE : apiDocConfig.getYapiEnable());
            apiDocConfig.setApifoxEnable(Objects.isNull(apiDocConfig.getApifoxEnable()) ? Boolean.FALSE : apiDocConfig.getApifoxEnable());
        }
        return apiDocConfig;
    }

    @Override
    public void loadState(@NotNull ApiDocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}

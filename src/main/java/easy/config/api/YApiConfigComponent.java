package easy.config.api;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.TreeMap;

@State(name = Constants.PLUGIN_NAME + "YApiConfig", storages = {@Storage(Constants.PLUGIN_NAME + "YApiConfig.xml")})
public class YApiConfigComponent implements PersistentStateComponent<YApiConfig> {

    private YApiConfig yApiConfig;

    @Override
    public @Nullable YApiConfig getState() {
        if (Objects.isNull(yApiConfig)) {
            yApiConfig = new YApiConfig();
            yApiConfig.setApiEnable(Boolean.TRUE);
            yApiConfig.setYapiTableMap(new TreeMap<>());
        } else {
            yApiConfig.setApiEnable(Objects.isNull(yApiConfig.getApiEnable()) ? Boolean.TRUE : yApiConfig.getApiEnable());
            yApiConfig.setYapiTableMap(MapUtil.isEmpty(yApiConfig.getYapiTableMap()) ? new TreeMap<>() : yApiConfig.getYapiTableMap());
        }
        return yApiConfig;
    }

    @Override
    public void loadState(@NotNull YApiConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}

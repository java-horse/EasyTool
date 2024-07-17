package easy.config.widget;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;

@State(name = Constants.PLUGIN_NAME + "WidgetConfig", storages = {@Storage(Constants.PLUGIN_NAME + "WidgetConfig.xml")})
public class WidgetConfigComponent implements PersistentStateComponent<WidgetConfig> {

    private WidgetConfig widgetConfig;

    @Override
    public @Nullable WidgetConfig getState() {
        if (Objects.isNull(widgetConfig)) {
            widgetConfig = new WidgetConfig();
            widgetConfig.setCronCollectionMap(new LinkedHashMap<>(16));
            widgetConfig.setWidgetCoreTabSet(new HashSet<>(16));
            widgetConfig.setCalculatorHistoryMap(new LinkedHashMap<>(16));
            widgetConfig.setSignConfig(new WidgetConfig.SignConfig());
        } else {
            widgetConfig.setCronCollectionMap(MapUtils.isEmpty(widgetConfig.getCronCollectionMap()) ? new LinkedHashMap<>(16) : widgetConfig.getCronCollectionMap());
            widgetConfig.setWidgetCoreTabSet(CollectionUtils.isEmpty(widgetConfig.getWidgetCoreTabSet()) ? new HashSet<>(16) : widgetConfig.getWidgetCoreTabSet());
            widgetConfig.setCalculatorHistoryMap(MapUtils.isEmpty(widgetConfig.getCalculatorHistoryMap()) ? new LinkedHashMap<>(16) : widgetConfig.getCalculatorHistoryMap());
            widgetConfig.setSignConfig(Objects.isNull(widgetConfig.getSignConfig()) ? new WidgetConfig.SignConfig() : widgetConfig.getSignConfig());
        }
        return widgetConfig;
    }

    @Override
    public void loadState(@NotNull WidgetConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }


}

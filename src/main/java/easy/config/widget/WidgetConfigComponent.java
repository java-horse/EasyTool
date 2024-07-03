package easy.config.widget;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import easy.enums.WidgetCoreTabEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;

@State(name = Constants.PLUGIN_NAME + "WidgetConfig", storages = {@Storage(Constants.PLUGIN_NAME + "WidgetConfig.xml")})
public class WidgetConfigComponent implements PersistentStateComponent<WidgetConfig> {

    private WidgetConfig widgetConfig;

    @Override
    public @Nullable WidgetConfig getState() {
        if (Objects.isNull(widgetConfig)) {
            widgetConfig = new WidgetConfig();
            widgetConfig.setCronCollectionMap(new LinkedHashMap<>(16));
            widgetConfig.setWidgetCoreTabSet(getWidgetCoreTabSet());
        } else {
            widgetConfig.setCronCollectionMap(MapUtils.isEmpty(widgetConfig.getCronCollectionMap()) ? new LinkedHashMap<>(16) : widgetConfig.getCronCollectionMap());
            widgetConfig.setWidgetCoreTabSet(CollectionUtils.isEmpty(widgetConfig.getWidgetCoreTabSet()) ? getWidgetCoreTabSet() : widgetConfig.getWidgetCoreTabSet());
        }
        return widgetConfig;
    }

    @Override
    public void loadState(@NotNull WidgetConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

    private LinkedHashSet<String> getWidgetCoreTabSet() {
        LinkedHashSet<String> tabSet = new LinkedHashSet<>(16);
        Arrays.stream(WidgetCoreTabEnum.values()).forEach(widgetCoreTabEnum -> tabSet.add(widgetCoreTabEnum.getTitle()));
        return tabSet;
    }

}

package easy.config.widget;

import java.util.LinkedHashMap;
import java.util.Set;

public class WidgetConfig {

    /**
     * 小部件核心选项卡集合
     */
    private Set<String> widgetCoreTabSet;

    /**
     * Cron集合映射
     */
    private LinkedHashMap<String, String> cronCollectionMap;


    public Set<String> getWidgetCoreTabSet() {
        return widgetCoreTabSet;
    }

    public void setWidgetCoreTabSet(Set<String> widgetCoreTabSet) {
        this.widgetCoreTabSet = widgetCoreTabSet;
    }

    public LinkedHashMap<String, String> getCronCollectionMap() {
        return cronCollectionMap;
    }

    public void setCronCollectionMap(LinkedHashMap<String, String> cronCollectionMap) {
        this.cronCollectionMap = cronCollectionMap;
    }

}

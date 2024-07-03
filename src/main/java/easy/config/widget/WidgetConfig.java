package easy.config.widget;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class WidgetConfig {

    /**
     * 小部件核心选项卡集合
     */
    private LinkedHashSet<String> widgetCoreTabSet;

    /**
     * Cron集合映射
     */
    private LinkedHashMap<String, String> cronCollectionMap;


    public LinkedHashSet<String> getWidgetCoreTabSet() {
        return widgetCoreTabSet;
    }

    public void setWidgetCoreTabSet(LinkedHashSet<String> widgetCoreTabSet) {
        this.widgetCoreTabSet = widgetCoreTabSet;
    }

    public LinkedHashMap<String, String> getCronCollectionMap() {
        return cronCollectionMap;
    }

    public void setCronCollectionMap(LinkedHashMap<String, String> cronCollectionMap) {
        this.cronCollectionMap = cronCollectionMap;
    }

}

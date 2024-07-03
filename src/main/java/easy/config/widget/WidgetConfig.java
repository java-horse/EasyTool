package easy.config.widget;

import java.util.LinkedHashMap;

public class WidgetConfig {

    /**
     * Cron集合映射
     */
    private LinkedHashMap<String, String> cronCollectionMap;



    public LinkedHashMap<String, String> getCronCollectionMap() {
        return cronCollectionMap;
    }

    public void setCronCollectionMap(LinkedHashMap<String, String> cronCollectionMap) {
        this.cronCollectionMap = cronCollectionMap;
    }

}

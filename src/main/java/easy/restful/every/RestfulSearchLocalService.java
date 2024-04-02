package easy.restful.every;

import easy.restful.search.RestServiceItem;
import easy.restful.search.RestServiceItemLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestfulSearchLocalService {

    private final List<RestServiceItem> restServiceItemList = new ArrayList<>();
    private final List<RestServiceItemLog> historyServiceItemList = new ArrayList<>();
    private final Map<String, RestServiceItem> historyServiceItemMap = new ConcurrentHashMap<>(16);


    public void addRestServiceItem(RestServiceItem restServiceItem) {
        restServiceItemList.add(restServiceItem);
    }

    public List<RestServiceItem> getRestServiceItemList() {
        return restServiceItemList;
    }

    public void addRestServiceItemLog(RestServiceItemLog restServiceItemLog) {
        historyServiceItemList.add(restServiceItemLog);
    }

    public List<RestServiceItemLog> getHistoryServiceItemList() {
        return historyServiceItemList;
    }

    public void putRestServiceItem(RestServiceItem restServiceItem) {
        historyServiceItemMap.put(restServiceItem.getName(), restServiceItem);
    }

    public Map<String, RestServiceItem> getHistoryServiceItemMap() {
        return historyServiceItemMap;
    }


}

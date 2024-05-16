package easy.config.api;

import easy.api.model.yapi.YApiTableDTO;

import java.util.Map;

public class YApiConfig {

    private String apiServerUrl;
    private Boolean apiEnable;
    private Map<String, YApiTableDTO> yapiTableMap;

    public String getApiServerUrl() {
        return apiServerUrl;
    }

    public void setApiServerUrl(String apiServerUrl) {
        this.apiServerUrl = apiServerUrl;
    }

    public Boolean getApiEnable() {
        return apiEnable;
    }

    public void setApiEnable(Boolean apiEnable) {
        this.apiEnable = apiEnable;
    }

    public Map<String, YApiTableDTO> getYapiTableMap() {
        return yapiTableMap;
    }

    public void setYapiTableMap(Map<String, YApiTableDTO> yapiTableMap) {
        this.yapiTableMap = yapiTableMap;
    }

}

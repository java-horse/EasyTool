package easy.config.api;

import javax.swing.*;

/**
 * API文档配置
 *
 * @author mabin
 * @project EasyTool
 * @package easy.config.api
 * @date 2024/04/01 11:19
 */
public class ApiDocConfig {

    private String yapiServer;
    private String yapiToken;
    private Boolean yapiEnable;
    private String apifoxServer;
    private String apifoxToken;
    private Boolean apifoxEnable;
    public String getYapiServer() {
        return yapiServer;
    }

    public void setYapiServer(String yapiServer) {
        this.yapiServer = yapiServer;
    }

    public String getYapiToken() {
        return yapiToken;
    }

    public void setYapiToken(String yapiToken) {
        this.yapiToken = yapiToken;
    }

    public String getApifoxServer() {
        return apifoxServer;
    }

    public void setApifoxServer(String apifoxServer) {
        this.apifoxServer = apifoxServer;
    }

    public String getApifoxToken() {
        return apifoxToken;
    }

    public void setApifoxToken(String apifoxToken) {
        this.apifoxToken = apifoxToken;
    }

    public Boolean getYapiEnable() {
        return yapiEnable;
    }

    public void setYapiEnable(Boolean yapiEnable) {
        this.yapiEnable = yapiEnable;
    }

    public Boolean getApifoxEnable() {
        return apifoxEnable;
    }

    public void setApifoxEnable(Boolean apifoxEnable) {
        this.apifoxEnable = apifoxEnable;
    }
}

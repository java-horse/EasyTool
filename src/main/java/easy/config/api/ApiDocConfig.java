package easy.config.api;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API文档配置
 *
 * @author mabin
 * @project EasyTool
 * @package easy.config.api
 * @date 2024/04/01 11:19
 */
public class ApiDocConfig {

    /**
     * 项目方法唯一标识
     * key:方法全路径 value:随机字符串
     */
    private Map<String, String> methodIdMap = new ConcurrentHashMap<>(16);

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

    public Map<String, String> getMethodIdMap() {
        return methodIdMap;
    }

    public void setMethodIdMap(Map<String, String> methodIdMap) {
        this.methodIdMap = methodIdMap;
    }

    /**
     * 获取全局方法id
     *
     * @param methodFullPath 方法全路径
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 16:10
     */
    public String getMethodId(String methodFullPath) {
        if (StringUtils.isBlank(methodFullPath)) {
            return StringUtils.EMPTY;
        }
        String methodId = methodIdMap.get(methodFullPath);
        if (StringUtils.isBlank(methodId)) {
            methodId = IdUtil.getSnowflakeNextIdStr();
            methodIdMap.put(methodFullPath, methodId);
        }
        return methodId;
    }

}

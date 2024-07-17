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

    /**
     * 计算历史集合映射
     */
    private LinkedHashMap<String, String> calculatorHistoryMap;

    /**
     * 签到配置
     */
    private SignConfig signConfig;

    /**
     * 钉钉Bot密钥
     */
    private String dingBotSecret;


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

    public LinkedHashMap<String, String> getCalculatorHistoryMap() {
        return calculatorHistoryMap;
    }

    public void setCalculatorHistoryMap(LinkedHashMap<String, String> calculatorHistoryMap) {
        this.calculatorHistoryMap = calculatorHistoryMap;
    }

    public SignConfig getSignConfig() {
        return signConfig;
    }

    public void setSignConfig(SignConfig signConfig) {
        this.signConfig = signConfig;
    }

    public static class SignConfig {
        private String cookie;
        private Integer reserved;
        private Boolean drawSwitch;
        private Long drawInternal;

        public String getCookie() {
            return cookie;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }

        public Integer getReserved() {
            return reserved;
        }

        public void setReserved(Integer reserved) {
            this.reserved = reserved;
        }

        public Boolean getDrawSwitch() {
            return drawSwitch;
        }

        public void setDrawSwitch(Boolean drawSwitch) {
            this.drawSwitch = drawSwitch;
        }

        public Long getDrawInternal() {
            return drawInternal;
        }

        public void setDrawInternal(Long drawInternal) {
            this.drawInternal = drawInternal;
        }
    }
}

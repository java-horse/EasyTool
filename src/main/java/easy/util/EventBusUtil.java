package easy.util;

import com.google.common.eventbus.EventBus;

public class EventBusUtil {

    private EventBusUtil() {
    }

    private static final EventBus EVENT_BUS = new EventBus();

    public static void post(Object event) {
        EVENT_BUS.post(event);
    }

    public static void register(Object subscriber) {
        EVENT_BUS.register(subscriber);
    }

    /**
     * 翻译备份事件实例
     *
     * @author mabin
     * @project EasyTool
     * @package easy.util.EventBusUtil
     * @date 2024/07/25 10:46
     */
    public static class TranslateBackUpEvent {
        private Integer id;
        /**
         * 源数据
         */
        private String source;
        /**
         * 目标数据
         */
        private String target;
        /**
         * 翻译渠道
         */
        private String channel;
        private String createTime;
        private String modifiedTime;
        private String ide;

        public TranslateBackUpEvent(String source, String target, String channel) {
            this.source = source;
            this.target = target;
            this.channel = channel;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getModifiedTime() {
            return modifiedTime;
        }

        public void setModifiedTime(String modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        public String getIde() {
            return ide;
        }

        public void setIde(String ide) {
            this.ide = ide;
        }
    }


}

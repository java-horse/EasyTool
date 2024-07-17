package easy.handler.ding;

public enum DingTypeEnum {

    TEXT("text", "文本消息"),
    NEWS("feedCard", "图文消息"),
    MARKDOWN("markdown", "Markdown消息"),
    LINK("link", "链接消息"),
    ACTION_CARD("actionCard", "跳转卡片消息");

    /**
     * 类型
     */
    private final String type;

    /**
     * 类型描述
     */
    private final String desc;

    DingTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}

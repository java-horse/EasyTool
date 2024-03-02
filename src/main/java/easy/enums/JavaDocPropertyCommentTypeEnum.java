package easy.enums;

public enum JavaDocPropertyCommentTypeEnum {
    SINGLE("单行模式"),
    ORDINARY("普通模式");

    private final String type;

    JavaDocPropertyCommentTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

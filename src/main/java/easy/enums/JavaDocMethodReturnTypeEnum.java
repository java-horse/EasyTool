package easy.enums;

public enum JavaDocMethodReturnTypeEnum {
    LINK("Link模式"),
    CODE("Code模式"),
    COMMENT("Comment模式");

    private final String type;

    JavaDocMethodReturnTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

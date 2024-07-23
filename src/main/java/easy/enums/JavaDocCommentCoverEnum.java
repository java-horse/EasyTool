package easy.enums;

public enum JavaDocCommentCoverEnum {

    MERGE("智能合并"),
    COVER("强制覆盖");

    private final String model;

    JavaDocCommentCoverEnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

}

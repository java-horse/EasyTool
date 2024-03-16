package easy.enums;

public enum JavaDocPropertyCommentModelEnum {

    JAVA_DOC("JavaDoc注释"),
    ORDINARY("普通文本注释");

    private final String model;

    JavaDocPropertyCommentModelEnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

}

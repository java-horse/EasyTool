package easy.enums;

public enum ApiDocContentType {

    URLENCODED("application/x-www-form-urlencoded", ""),
    FORM_DATA("multipart/form-data", "form"),
    JSON("application/json", "json"),
    RAW("application/raw", "raw");

    private final String type;

    private final String desc;

    ApiDocContentType(String type, String desc) {
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

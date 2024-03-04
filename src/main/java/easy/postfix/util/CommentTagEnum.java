package easy.postfix.util;


import java.util.HashMap;
import java.util.Map;

/**
 * 常规注释
 */
public enum CommentTagEnum {

    /**
     * 常规注释
     */
    DEFAULT("value"),
    HIDDEN("hidden", true),
    REQUIRED("required", true),
    EXAMPLE("example"),
    NOTES("notes"),
    TAGS("tags"),
    IMPORTANT("important", true),
    DESCRIPTION("description");

    private final String tag;
    private final boolean boolType;

    public String getTag() {
        return tag;
    }

    public boolean isBoolType() {
        return boolType;
    }

    CommentTagEnum(String tag) {
        this.tag = tag;
        this.boolType = false;
    }

    CommentTagEnum(String tag, Boolean boolType) {
        this.tag = tag;
        this.boolType = boolType;
    }

    public static CommentTagEnum of(String tag) {
        for (CommentTagEnum commentTagEnum : values()) {
            if (commentTagEnum.getTag().equals(tag)) {
                return commentTagEnum;
            }
        }
        return CommentTagEnum.DEFAULT;
    }

    public static Map<String, CommentTagEnum> allTagMap() {
        Map<String, CommentTagEnum> allTagMap = new HashMap<>(32);
        for (CommentTagEnum commentTagEnum : values()) {
            allTagMap.put(commentTagEnum.getTag(), commentTagEnum);
        }
        return allTagMap;
    }

}

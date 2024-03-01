package easy.enums;


import org.apache.commons.lang3.StringUtils;

public enum JavaDocVariableTypeEnum {

    STRING("固定值"),
    GROOVY("Groovy脚本");

    private final String desc;

    JavaDocVariableTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static JavaDocVariableTypeEnum fromDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return null;
        }
        for (JavaDocVariableTypeEnum value : values()) {
            if (value.desc.equals(desc)) {
                return value;
            }
        }
        return null;
    }

}

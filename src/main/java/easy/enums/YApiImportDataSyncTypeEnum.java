package easy.enums;

import java.util.Objects;

public enum YApiImportDataSyncTypeEnum {
    NORMAL( "normal", "普通模式"),
    GOOD( "good", "智能合并"),
    MERGE( "merge", "完全覆盖");

    private final String code;
    private final String desc;

    YApiImportDataSyncTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getCode(String desc) {
        if (Objects.isNull(desc) || desc.isBlank()) {
            return "";
        }
        for (YApiImportDataSyncTypeEnum syncTypeEnum : values()) {
            if (syncTypeEnum.getDesc().equals(desc)) {
                return syncTypeEnum.getCode();
            }
        }
        return "";
    }

}

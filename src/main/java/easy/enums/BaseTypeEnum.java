package easy.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 基本数据类型枚举处理
 *
 * @project: EasyChar
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/11/04 10:08:32
 */
public enum BaseTypeEnum {

    BYTE("byte", "java.lang.Byte"),
    CHAR("char", "java.lang.Character"),
    DOUBLE("double", "java.lang.Double"),
    FLOAT("float", "java.lang.Float"),
    INT("int", "java.lang.Integer"),
    LONG("long", "java.lang.Long"),
    SHORT("short", "java.lang.Short"),
    BOOLEAN("boolean", "java.lang.Boolean"),
    STRING("string", "java.lang.String");

    private final String baseType;
    private final String boxedType;


    public static String findBaseType(String boxedType) {
        if (Objects.isNull(boxedType)) {
            return StringUtils.EMPTY;
        }
        for (BaseTypeEnum anEnum : allEnums()) {
            if (Objects.equals(anEnum.boxedType, boxedType)) {
                return anEnum.baseType;
            }
        }
        return StringUtils.EMPTY;
    }

    public static Boolean isBaseType(String baseType) {
        if (Objects.isNull(baseType)) {
            return Boolean.FALSE;
        }
        for (BaseTypeEnum anEnum : allEnums()) {
            if (Objects.equals(anEnum.getBaseType(), baseType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private static BaseTypeEnum[] allEnums() {
        return new BaseTypeEnum[]{BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT, BOOLEAN, STRING};
    }

    BaseTypeEnum(String baseType, String boxedType) {
        this.baseType = baseType;
        this.boxedType = boxedType;
    }

    public String getBaseType() {
        return baseType;
    }

    public String getBoxedType() {
        return boxedType;
    }

}

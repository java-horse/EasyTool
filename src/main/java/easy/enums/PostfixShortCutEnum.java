package easy.enums;

/**
 * Postfix快捷方式名称枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2024/01/20 10:44:02
 */
public enum PostfixShortCutEnum {

    GENERATE_GETTER("Generate Getter"),
    GENERATE_SETTER("Generate Setter"),
    GENERATE_SETTER_NO_DEFAULT_VAL("Generate Setter with no default val"),
    GENERATE_CONVERT("Generate Convert");

    private final String name;

    PostfixShortCutEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

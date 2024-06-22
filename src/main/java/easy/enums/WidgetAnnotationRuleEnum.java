package easy.enums;

import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;

public enum WidgetAnnotationRuleEnum {

    CAMEL_CASE("CamelCase") {
        @Override
        public String genName(@NotNull String name) {
            return StrUtil.toCamelCase(name);
        }
    },

    ORIGINAL("Original") {
        @Override
        public String genName(@NotNull String name) {
            return name;
        }
    },
    UPPER_CASE("UpperCase") {
        @Override
        public String genName(@NotNull String name) {
            return name.toUpperCase();
        }
    },
    LOWER_CASE("LowerCase") {
        @Override
        public String genName(@NotNull String name) {
            return name.toLowerCase();
        }
    },
    UNDERLINE("Underline") {
        @Override
        public String genName(@NotNull String name) {
            return StrUtil.toUnderlineCase(name);
        }
    };

    private final String rule;

    WidgetAnnotationRuleEnum(String rule) {
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    /**
     * 根据规则生成名称
     *
     * @param name 姓名
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/06/19 16:09
     */
    public abstract String genName(@NotNull String name);

    public static WidgetAnnotationRuleEnum getEnum(String rule) {
        for (WidgetAnnotationRuleEnum value : values()) {
            if (value.getRule().equals(rule)) {
                return value;
            }
        }
        return null;
    }

}

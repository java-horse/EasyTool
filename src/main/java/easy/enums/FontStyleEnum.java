package easy.enums;

import java.awt.*;
import java.util.Objects;

public enum FontStyleEnum {
    PLAIN("PLAIN", Font.PLAIN),
    BOLD("BOLD", Font.BOLD),
    ITALIC("ITALIC", Font.ITALIC);

    private final String name;
    private final Integer value;

    FontStyleEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static Integer getFontStyle(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            return PLAIN.value;
        }
        for (FontStyleEnum styleEnum : values()) {
            if (styleEnum.name.equals(name)) {
                return styleEnum.value;
            }
        }
        return PLAIN.value;
    }

}

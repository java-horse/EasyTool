package easy.api.model.common;

import org.apache.commons.lang3.StringUtils;

public class Value {

    private final String value;
    private final String description;

    public Value(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Value{" +
                "value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


    /**
     * 获取描述文本
     *
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/11 14:23
     */
    public String getText() {
        if (StringUtils.isBlank(description)) {
            return value;
        }
        return value + ": " + description;
    }

}

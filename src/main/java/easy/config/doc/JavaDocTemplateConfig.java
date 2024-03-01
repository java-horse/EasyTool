package easy.config.doc;

import easy.enums.JavaDocVariableTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class JavaDocTemplateConfig {

    public JavaDocTemplateConfig() {
        isDefault = true;
        template = StringUtils.EMPTY;
        customMap = new TreeMap<>();
    }

    private Boolean isDefault;
    private String template;
    private Map<String, CustomValue> customMap;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, CustomValue> getCustomMap() {
        return customMap;
    }

    public void setCustomMap(Map<String, CustomValue> customMap) {
        this.customMap = customMap;
    }


    public static class CustomValue {

        private JavaDocVariableTypeEnum javaDocVariableTypeEnum;

        private String value;

        public CustomValue() {
        }

        public CustomValue(JavaDocVariableTypeEnum javaDocVariableTypeEnum, String value) {
            this.javaDocVariableTypeEnum = javaDocVariableTypeEnum;
            this.value = value;
        }

        public JavaDocVariableTypeEnum getJavaDocVariableTypeEnum() {
            return javaDocVariableTypeEnum;
        }

        public void setJavaDocVariableTypeEnum(JavaDocVariableTypeEnum javaDocVariableTypeEnum) {
            this.javaDocVariableTypeEnum = javaDocVariableTypeEnum;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}

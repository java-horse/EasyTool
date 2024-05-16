package easy.api.model.common;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * 参数
 */
public class Property {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 格式
     */
    private String format;

    /**
     * 时间格式
     */
    private String dateFormat;

    /**
     * 描述
     */
    private String description;

    /**
     * 参数位置
     */
    private ParameterIn in;

    /**
     * 是否必须
     */
    private Boolean required;

    /**
     * 是否标记过期
     */
    private Boolean deprecated;

    /**
     * 请求示例
     */
    private String example;

    /**
     * 响应mock
     */
    private String mock;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 值列表
     */
    private List<Value> values;

    /**
     * 当type为array
     */
    private Property items;

    /**
     * 当type为array, item元素是否唯一
     */
    private Boolean uniqueItems;

    /**
     * 最小元素个数
     */
    @SerializedName(value = "minLength", alternate = "minItems")
    private Integer minLength;

    /**
     * 最大元素个数
     */
    @SerializedName(value = "maxLength", alternate = "maxItems")
    private Integer maxLength;

    /**
     * 当type为object
     */
    private Map<String, Property> properties;

    /**
     * 最小值
     */
    private BigDecimal minimum = null;

    /**
     * 最大值
     */
    private BigDecimal maximum = null;

    public boolean isArrayType() {
        return DataTypes.ARRAY.equals(type);
    }

    public boolean isObjectType() {
        return DataTypes.OBJECT.equals(type);
    }

    public boolean isStringType() {
        return DataTypes.STRING.equals(type);
    }

    public boolean isNumberOrIntegerType() {
        return DataTypes.NUMBER.equals(type) || DataTypes.INTEGER.equals(type);
    }

    public boolean isNumberType() {
        return DataTypes.NUMBER.equals(type);
    }

    public boolean isIntegerType() {
        return DataTypes.INTEGER.equals(type);
    }

    public boolean isFileType() {
        return DataTypes.FILE.equals(type);
    }

    /**
     * 获取类型名称, 包括数组
     */
    public String getTypeWithArray() {
        if (!DataTypes.ARRAY.equals(this.type)) {
            return this.type;
        }
        if (this.items == null) {
            return DataTypes.OBJECT + "[]";
        }
        return this.items.type + "[]";
    }

    /**
     * 合并自定义配置, 自定义优先
     */
    public void mergeCustom(Property custom) {
        if (StringUtils.isNotEmpty(custom.getName())) {
            this.name = custom.getName();
        }
        if (StringUtils.isNotEmpty(custom.getType())) {
            this.type = custom.getType();
        }
        if (StringUtils.isNotEmpty(custom.getDescription())) {
            this.description = custom.getDescription();
        }
        if (custom.getRequired() != null) {
            this.required = custom.getRequired();
        }
        if (custom.getDeprecated() != null) {
            this.deprecated = custom.getDeprecated();
        }
        if (StringUtils.isNotEmpty(custom.getDefaultValue())) {
            this.defaultValue = custom.getDefaultValue();
        }
        if (StringUtils.isNotEmpty(custom.getExample())) {
            this.example = custom.getExample();
        }
        if (StringUtils.isNotEmpty(custom.getMock())) {
            this.mock = custom.getMock();
        }
        if (custom.getMaxLength() != null) {
            this.maxLength = custom.getMaxLength();
        }
        if (custom.getMinLength() != null) {
            this.minLength = custom.getMinLength();
        }
        if (custom.getUniqueItems() != null) {
            this.uniqueItems = custom.getUniqueItems();
        }
    }

    /**
     * 获取可能的值
     */
    public List<String> getValueList() {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream().map(Value::getValue).collect(Collectors.toList());
    }

    public List<Value> getPropertyValues() {
        Property item = this;
        List<Value> values = item.getValues();
        boolean isArrayEnum = DataTypes.ARRAY.equals(item.getType())
                && item.getItems() != null
                && CollectionUtils.isNotEmpty(item.getItems().getValues());
        if (isArrayEnum) {
            values = item.getItems().getValues();
        }
        return values;
    }

    public void addProperty(String key, Property value) {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }
        this.properties.put(key, value);
    }

    public String getDescriptionMore() {
        Property property = this;
        String description = property.getDescription() != null ? property.getDescription() : "";
        List<String> attaches = Lists.newArrayList();
        // 长度范围
        if (property.getMinLength() != null || property.getMaxLength() != null) {
            int min = property.getMinLength() != null ? property.getMinLength() : 0;
            int max = property.getMaxLength() != null ? property.getMaxLength() : Integer.MAX_VALUE;
            attaches.add(format("长度: %d~%d", min, max));
        }
        // 数值范围
        if (property.getMinimum() != null || property.getMaximum() != null) {
            String min = property.getMinimum() != null ? property.getMinimum().toPlainString() : "";
            String max = property.getMaximum() != null ? property.getMaximum().toPlainString() : "";
            attaches.add(format("大小: %s~%s", min, max));
        }
        if (!attaches.isEmpty()) {
            description += " [" + String.join(", ", attaches) + "]";
        }
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParameterIn getIn() {
        return in;
    }

    public void setIn(ParameterIn in) {
        this.in = in;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public Property getItems() {
        return items;
    }

    public void setItems(Property items) {
        this.items = items;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }
}

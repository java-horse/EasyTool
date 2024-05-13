package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Set;

/**
 * 参数
 */
public class ApiProperty {

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必须
     */
    private Set<String> required;

    @SerializedName("default")
    private String defaultValue;

    /**
     * 当type为object
     */
    private Map<String, ApiProperty> properties;

    /**
     * 当type为array
     */
    private ApiProperty items;

    /**
     * 当type为array, item元素是否唯一
     */
    private Boolean uniqueItems;

    /**
     * 当type为array, 最小元素个数
     */
    private Integer minItems;

    /**
     * 当type为array, 最大元素个数
     */
    private Integer maxItems;

    /**
     * 字符串长度
     */
    private Integer minLength;

    /**
     * 字符串长度
     */
    private Integer maxLength;

    /**
     * 响应mock
     */
    private Mock mock;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRequired() {
        return required;
    }

    public void setRequired(Set<String> required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, ApiProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ApiProperty> properties) {
        this.properties = properties;
    }

    public ApiProperty getItems() {
        return items;
    }

    public void setItems(ApiProperty items) {
        this.items = items;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
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

    public Mock getMock() {
        return mock;
    }

    public void setMock(Mock mock) {
        this.mock = mock;
    }

    public static class Mock {

        private String mock;

        public Mock(String mock) {
            this.mock = mock;
        }

        public String getMock() {
            return mock;
        }

        public void setMock(String mock) {
            this.mock = mock;
        }
    }
}



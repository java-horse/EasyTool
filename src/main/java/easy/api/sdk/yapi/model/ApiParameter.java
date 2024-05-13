package easy.api.sdk.yapi.model;


import java.io.Serial;
import java.io.Serializable;

/**
 * YApi接口表单参数
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/10 18:17
 */
public class ApiParameter implements Serializable {
    @Serial
    private static final long serialVersionUID = -6972634460363382307L;

    /**
     * 参数名字
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String desc;

    /**
     * 是否必填
     */
    private String required;

    /**
     * 示例
     */
    private String example;

    /**
     * 值
     */
    private String value;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package easy.api.entity;

public class YApiParamInfo {

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数实例
     */
    private String example;

    /**
     * 参数描述
     */
    private String desc;

    /**
     * 枚举: 1,0
     */
    private String required;

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

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
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
}

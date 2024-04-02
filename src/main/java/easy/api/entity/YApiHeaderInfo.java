package easy.api.entity;

public class YApiHeaderInfo {

    /**
     * 请求头名称
     */
    private String name;

    /**
     * 请求头value
     */
    private String value;

    /**
     * 请求头示例
     */
    private String example;

    /**
     * 请求头描述
     */
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}

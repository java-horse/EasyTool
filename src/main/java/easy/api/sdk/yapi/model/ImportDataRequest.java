package easy.api.sdk.yapi.model;

import java.io.Serial;
import java.io.Serializable;

public class ImportDataRequest extends YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -7839274664780164899L;

    /**
     * 导入方式 swagger, postman
     */
    private String type;

    /**
     * 数据同步方式 normal"(普通模式) , "good"(智能合并), "merge"(完全覆盖) 三种模式
     */
    private String merge;

    /**
     * JSON数据
     */
    private String json;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerge() {
        return merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}

package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;


/**
 * YApi菜单创建请求
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/10 18:10
 */
public class CategoryCreateRequest extends YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -7678325408090965411L;

    @SerializedName("project_id")
    private Integer projectId;

    private String name;

    private String desc;

    @SerializedName("parent_id")
    private Integer parentId = -1;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}

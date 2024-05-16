package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;

/**
 * YApi分类列表
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/10 15:36
 */
public class ApiCategory extends YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -5603822954082117333L;

    @SerializedName("_id")
    private Integer id;

    private String name;

    @SerializedName("project_id")
    private Integer projectId;

    private String desc;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public String getDesc() {
        return desc;
    }
}

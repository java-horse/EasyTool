package easy.api.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;

public class YApiCategoryInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8973949617382225475L;

    @SerializedName("_id")
    private Long categoryId;

    @SerializedName("project_id")
    private Long projectId;

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
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
}

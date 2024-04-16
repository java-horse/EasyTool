package easy.api.entity;

import com.google.gson.annotations.SerializedName;

/**
 * YAPI项目信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.entity
 * @date 2024/04/01 14:21
 */
public class YApiProjectInfo {

    @SerializedName("group_id")
    private Integer groupId;

    @SerializedName("_id")
    private Long projectId;

    @SerializedName("name")
    private String projectName;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}

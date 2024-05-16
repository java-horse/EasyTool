package easy.api.model.yapi;

import java.io.Serial;
import java.io.Serializable;

public class YApiTableDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8542020042799752139L;

    /**
     * 是否选中
     */
    private Boolean select;

    /**
     * 项目token
     */
    private String projectToken;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    public YApiTableDTO() {
    }

    public YApiTableDTO(String projectId, String projectName, String projectToken) {
        this.select = Boolean.FALSE;
        this.projectToken = projectToken;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public String getProjectToken() {
        return projectToken;
    }

    public void setProjectToken(String projectToken) {
        this.projectToken = projectToken;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}

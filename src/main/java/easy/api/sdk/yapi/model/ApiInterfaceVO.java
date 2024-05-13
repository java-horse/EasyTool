package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * YApi接口描述信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/11 09:54
 */
public class ApiInterfaceVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6912546085803639004L;

    @SerializedName("_id")
    private int id;

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("catid")
    private int catId;

    @SerializedName("title")
    private String title;

    @SerializedName("method")
    private String method;

    @SerializedName("path")
    private String path;

    @SerializedName("tag")
    private List<?> tag;

    @SerializedName("edit_uid")
    private int editUid;

    @SerializedName("status")
    private String status;

    @SerializedName("api_opened")
    private boolean apiOpened;

    @SerializedName("index")
    private int index;

    @SerializedName("uid")
    private int uid;

    @SerializedName("add_time")
    private int addTime;

    @SerializedName("up_time")
    private int upTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<?> getTag() {
        return tag;
    }

    public void setTag(List<?> tag) {
        this.tag = tag;
    }

    public int getEditUid() {
        return editUid;
    }

    public void setEditUid(int editUid) {
        this.editUid = editUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isApiOpened() {
        return apiOpened;
    }

    public void setApiOpened(boolean apiOpened) {
        this.apiOpened = apiOpened;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAddTime() {
        return addTime;
    }

    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }

    public int getUpTime() {
        return upTime;
    }

    public void setUpTime(int upTime) {
        this.upTime = upTime;
    }
}

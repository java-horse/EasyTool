package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * YApi接口详细信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/10 18:15
 */
public class ApiInterface extends YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -6871729269907590092L;


    private Integer id;

    @SerializedName("project_id")
    private Integer projectId;

    private String method;

    private String path;

    @SerializedName("req_params")
    private List<ApiParameter> reqParams;

    private String menu;

    private List<String> tag;

    @SerializedName("req_query")
    private List<ApiParameter> reqQuery;

    @SerializedName("req_headers")
    private List<ApiParameter> reqHeaders;

    @SerializedName("req_body_form")
    private List<ApiParameter> reqBodyForm;

    private String title;

    @SerializedName("catid")
    private Integer catId;

    @SerializedName("req_body_type")
    private String reqBodyType = "json";

    @SerializedName("req_body_other")
    private String reqBodyOther;

    @SerializedName("req_body_is_json_schema")
    private boolean reqBodyIsJsonSchema;

    private String status = "undone";

    @SerializedName("res_body_type")
    private String resBodyType = "json";

    @SerializedName("res_body")
    private String resBody;

    @SerializedName("res_body_is_json_schema")
    private boolean resBodyIsJsonSchema = true;

    @SerializedName("edit_uid")
    private Integer editUid = 11;

    private String username;

    @SerializedName("switch_notice")
    private boolean switchNotice;

    private String message = " ";

    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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

    public List<ApiParameter> getReqParams() {
        return reqParams;
    }

    public void setReqParams(List<ApiParameter> reqParams) {
        this.reqParams = reqParams;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<ApiParameter> getReqQuery() {
        return reqQuery;
    }

    public void setReqQuery(List<ApiParameter> reqQuery) {
        this.reqQuery = reqQuery;
    }

    public List<ApiParameter> getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(List<ApiParameter> reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public List<ApiParameter> getReqBodyForm() {
        return reqBodyForm;
    }

    public void setReqBodyForm(List<ApiParameter> reqBodyForm) {
        this.reqBodyForm = reqBodyForm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getReqBodyType() {
        return reqBodyType;
    }

    public void setReqBodyType(String reqBodyType) {
        this.reqBodyType = reqBodyType;
    }

    public String getReqBodyOther() {
        return reqBodyOther;
    }

    public void setReqBodyOther(String reqBodyOther) {
        this.reqBodyOther = reqBodyOther;
    }

    public boolean isReqBodyIsJsonSchema() {
        return reqBodyIsJsonSchema;
    }

    public void setReqBodyIsJsonSchema(boolean reqBodyIsJsonSchema) {
        this.reqBodyIsJsonSchema = reqBodyIsJsonSchema;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResBodyType() {
        return resBodyType;
    }

    public void setResBodyType(String resBodyType) {
        this.resBodyType = resBodyType;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }

    public boolean isResBodyIsJsonSchema() {
        return resBodyIsJsonSchema;
    }

    public void setResBodyIsJsonSchema(boolean resBodyIsJsonSchema) {
        this.resBodyIsJsonSchema = resBodyIsJsonSchema;
    }

    public Integer getEditUid() {
        return editUid;
    }

    public void setEditUid(Integer editUid) {
        this.editUid = editUid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSwitchNotice() {
        return switchNotice;
    }

    public void setSwitchNotice(boolean switchNotice) {
        this.switchNotice = switchNotice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

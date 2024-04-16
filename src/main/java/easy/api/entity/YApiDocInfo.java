package easy.api.entity;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 保存API信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.entity
 * @date 2024/04/02 16:17
 */
public class YApiDocInfo implements Serializable {
    private static final long serialVersionUID = -6950965685504922540L;

    /**
     * 项目 token
     */
    private String token;

    /**
     * 项目 id
     */
    private Long projectId;

    /**
     * 接口 id
     */
    private String id;

    /**
     * 品类id
     */
    @SerializedName("catid")
    private Long catId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 请求数据类型
     * 枚举: raw,form,json
     */
    @SerializedName("req_body_type")
    private String reqBodyType;

    /**
     * 请求数据body
     */
    @SerializedName("req_body_other")
    private String reqBodyOther;

    /**
     * 请求参数body 是否为json_schema
     */
    @SerializedName("req_body_is_json_schema")
    private boolean reqBodyIsJsonSchema;

    /**
     * 请求参数(form-data)
     */
    @SerializedName("req_body_form")
    private List<YApiParamInfo> reqBodyForm = Lists.newArrayList();

    /**
     * 请求参数(urlencoded)
     */
    @SerializedName("req_params")
    private List<YApiParamInfo> reqParams = Lists.newArrayList();

    /**
     * 请求头
     */
    @SerializedName("req_headers")
    private List<YApiHeaderInfo> reqHeaders = Lists.newArrayList();

    /**
     * 查询参数(GET请求)
     */
    @SerializedName("req_query")
    private List<YApiParamInfo> reqQuery = Lists.newArrayList();

    /**
     * 返回参数类型  json
     * 枚举: raw,json
     */
    @SerializedName("res_body_type")
    private String resBodyType = "json";

    /**
     * 返回参数
     */
    @SerializedName("res_body")
    private String resBody;

    /**
     * 文档描述
     */
    private String desc = StringUtils.EMPTY;

    /**
     * 标题
     */
    private String title;

    /**
     * 邮件开关
     */
    @SerializedName("switch_notice")
    private Boolean switchNotice = false;

    /**
     * 状态 undone,默认done
     */
    private String status = "done";


    /**
     * 返回参数是否为json_schema
     */
    @SerializedName("res_body_is_json_schema")
    private boolean resBodyIsJsonSchema = true;

    /**
     * 备注信息
     */
    private String markdown;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public List<YApiParamInfo> getReqBodyForm() {
        return reqBodyForm;
    }

    public void setReqBodyForm(List<YApiParamInfo> reqBodyForm) {
        this.reqBodyForm = reqBodyForm;
    }

    public List<YApiParamInfo> getReqParams() {
        return reqParams;
    }

    public void setReqParams(List<YApiParamInfo> reqParams) {
        this.reqParams = reqParams;
    }

    public List<YApiHeaderInfo> getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(List<YApiHeaderInfo> reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public List<YApiParamInfo> getReqQuery() {
        return reqQuery;
    }

    public void setReqQuery(List<YApiParamInfo> reqQuery) {
        this.reqQuery = reqQuery;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getSwitchNotice() {
        return switchNotice;
    }

    public void setSwitchNotice(Boolean switchNotice) {
        this.switchNotice = switchNotice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isResBodyIsJsonSchema() {
        return resBodyIsJsonSchema;
    }

    public void setResBodyIsJsonSchema(boolean resBodyIsJsonSchema) {
        this.resBodyIsJsonSchema = resBodyIsJsonSchema;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
}

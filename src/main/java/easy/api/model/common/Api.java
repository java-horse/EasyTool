package easy.api.model.common;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 接口信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.model.common
 * @date 2024/05/11 14:26
 */
public class Api {

    /**
     * 分类
     */
    private String category;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 路径
     */
    private String path;

    /**
     * 概述
     */
    private String summary;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 是否标记过期
     */
    private Boolean deprecated;

    /**
     * 参数
     */
    private List<Property> parameters;

    /**
     * 请求体类型
     */
    private RequestBodyType requestBodyType;

    /**
     * 请求体参数
     */
    private Property requestBody;

    /**
     * 请求体表单
     */
    private List<Property> requestBodyForm;

    /**
     * 响应体
     */
    private Property responses;


    /**
     * 获取指定类型的请求参数（query, path, header,etc）
     */
    public List<Property> getParametersByIn(ParameterIn in) {
        if (parameters == null) {
            return Collections.emptyList();
        }
        return parameters.stream().filter(p -> p.getIn() == in).collect(Collectors.toList());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public List<Property> getParameters() {
        return parameters;
    }

    public void setParameters(List<Property> parameters) {
        this.parameters = parameters;
    }

    public RequestBodyType getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public Property getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Property requestBody) {
        this.requestBody = requestBody;
    }

    public List<Property> getRequestBodyForm() {
        return requestBodyForm;
    }

    public void setRequestBodyForm(List<Property> requestBodyForm) {
        this.requestBodyForm = requestBodyForm;
    }

    public Property getResponses() {
        return responses;
    }

    public void setResponses(Property responses) {
        this.responses = responses;
    }
}

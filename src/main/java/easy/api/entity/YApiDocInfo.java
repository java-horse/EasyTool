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
}

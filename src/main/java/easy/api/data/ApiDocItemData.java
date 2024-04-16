package easy.api.data;


import java.util.List;
import java.util.Map;


/**
 * 生成每一个接口文档所需的数据（渲染一个接口的标准对象）
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.data
 * @date 2024/04/13 15:13
 */
public class ApiDocItemData extends CommonItemData {

    /**
     * 接口序号
     */
    private String docNo;

    /**
     * 请求示例
     */
    private String requestExample;

    /**
     * 请求示例数据类型
     */
    private String requestExampleType;


    /**
     * 请求参数
     */
    private List<ApiDocParamData> requestParams;

    /**
     * 响应示例
     */
    private String responseExample;
    /**
     * 响应示例数据类型
     */
    private String responseExampleType;

    /**
     * 响应参数
     */
    private List<ApiDocParamData> responseParams;

    /**
     * 扩展信息
     */
    private Map<String, Object> extDocMap;


    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getRequestExample() {
        return requestExample;
    }

    public void setRequestExample(String requestExample) {
        this.requestExample = requestExample;
    }

    public String getRequestExampleType() {
        return requestExampleType;
    }

    public void setRequestExampleType(String requestExampleType) {
        this.requestExampleType = requestExampleType;
    }

    public List<ApiDocParamData> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<ApiDocParamData> requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseExample() {
        return responseExample;
    }

    public void setResponseExample(String responseExample) {
        this.responseExample = responseExample;
    }

    public String getResponseExampleType() {
        return responseExampleType;
    }

    public void setResponseExampleType(String responseExampleType) {
        this.responseExampleType = responseExampleType;
    }

    public List<ApiDocParamData> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<ApiDocParamData> responseParams) {
        this.responseParams = responseParams;
    }

    public Map<String, Object> getExtDocMap() {
        return extDocMap;
    }

    public void setExtDocMap(Map<String, Object> extDocMap) {
        this.extDocMap = extDocMap;
    }
}

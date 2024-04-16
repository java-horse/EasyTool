package easy.api.data;


import java.util.Map;

public class ApiDocParamData {

    /**
     * 参数编号
     */
    private String paramNo;

    /**
     * 父级参数编号
     */
    private String parentParamNo;

    /**
     * 参数名前缀
     */
    private String paramPrefix;

    /**
     * 参数名
     */
    private String paramName;
    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 当paramType为array时 childParamType为array下具体的参数类型
     */
    private String childParamType;

    /**
     * 是否必填
     */
    private String paramRequire;

    /**
     * 参数描述信息
     */
    private String paramDesc;

    /**
     * mock的值
     */
    private String paramValue;

    /**
     * 扩展信息
     */
    private Map<String, Object> ext;

    public String getParamNo() {
        return paramNo;
    }

    public void setParamNo(String paramNo) {
        this.paramNo = paramNo;
    }

    public String getParentParamNo() {
        return parentParamNo;
    }

    public void setParentParamNo(String parentParamNo) {
        this.parentParamNo = parentParamNo;
    }

    public String getParamPrefix() {
        return paramPrefix;
    }

    public void setParamPrefix(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getChildParamType() {
        return childParamType;
    }

    public void setChildParamType(String childParamType) {
        this.childParamType = childParamType;
    }

    public String getParamRequire() {
        return paramRequire;
    }

    public void setParamRequire(String paramRequire) {
        this.paramRequire = paramRequire;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }
}

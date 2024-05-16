package easy.api.parse.model;

import com.intellij.psi.PsiMethod;
import easy.api.model.common.Api;

import java.util.Collections;
import java.util.List;

/**
 * 方法解析数据
 */
public class MethodApiData {

    /**
     * 是否是有效的接口方法
     */
    private boolean valid = true;

    /**
     * 目标方法
     */
    private PsiMethod method;


    /**
     * 指定的接口名称
     */
    private String declaredApiSummary;

    /**
     * 接口列表
     */
    private List<Api> apis;

    public List<Api> getApis() {
        return apis != null ? apis : Collections.emptyList();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public void setMethod(PsiMethod method) {
        this.method = method;
    }

    public String getDeclaredApiSummary() {
        return declaredApiSummary;
    }

    public void setDeclaredApiSummary(String declaredApiSummary) {
        this.declaredApiSummary = declaredApiSummary;
    }

    public void setApis(List<Api> apis) {
        this.apis = apis;
    }
}

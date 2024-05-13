package easy.api.parse.model;

import com.google.common.collect.Lists;
import easy.api.model.common.Api;

import java.util.Collections;
import java.util.List;

/**
 * 接口解析结果
 */
public class ClassApiData {

    /**
     * 有效的类
     */
    private boolean valid = true;

    /**
     * 声明的分类名称
     */
    private String declaredCategory;

    private List<MethodApiData> methodDataList;

    public List<Api> getApis() {
        if (methodDataList == null || methodDataList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Api> apis = Lists.newArrayList();
        for (MethodApiData methodApiInfo : methodDataList) {
            apis.addAll(methodApiInfo.getApis());
        }
        return apis;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getDeclaredCategory() {
        return declaredCategory;
    }

    public void setDeclaredCategory(String declaredCategory) {
        this.declaredCategory = declaredCategory;
    }

    public List<MethodApiData> getMethodDataList() {
        return methodDataList;
    }

    public void setMethodDataList(List<MethodApiData> methodDataList) {
        this.methodDataList = methodDataList;
    }
}

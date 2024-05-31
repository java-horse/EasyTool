package easy.api.sdk.yapi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import easy.api.sdk.yapi.model.*;
import easy.base.Constants;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * YApi客户端
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi
 * @date 2024/05/10 15:55
 */
public class YApiClient {

    private final String url;
    private final String token;
    private final YApiService apiService;

    public YApiClient(String url, String token) {
        this.url = StrUtil.endWith(url, "/") ? StrUtil.subBefore(url, "/", true) : url;
        this.token = StrUtil.trim(token);
        this.apiService = new YApiService(url, token);
    }

    /**
     * 获取YApi项目信息
     *
     * @return {@link easy.api.sdk.yapi.model.ApiProject}
     * @author mabin
     * @date 2024/05/10 17:06
     */
    public ApiProject getProject() {
        return apiService.getProject().getData();
    }

    /**
     * 获取YApi项目下所有分类菜单
     *
     * @param projectId 项目id
     * @return {@link java.util.List<easy.api.sdk.yapi.model.ApiCategory>}
     * @author mabin
     * @date 2024/05/10 17:08
     */
    public List<ApiCategory> getCategories(Integer projectId) {
        return apiService.getCategories(projectId).getData();
    }

    /**
     * 添加YApi类别
     *
     * @param request 请求
     * @return {@link easy.api.sdk.yapi.model.ApiCategory}
     * @author mabin
     * @date 2024/05/11 11:12
     */
    public ApiCategory addCategory(CategoryCreateRequest request) {
        return apiService.addCategory(request).getData();
    }

    /**
     * 获取YApi项目下单个接口详情
     *
     * @param interfaceId 接口id
     * @return {@link easy.api.sdk.yapi.model.ApiInterface}
     * @author mabin
     * @date 2024/05/11 09:39
     */
    public ApiInterface getApiInterface(Integer interfaceId) {
        return apiService.getApiInterface(interfaceId).getData();
    }

    /**
     * 保存YApi接口信息
     *
     * @param apiInterface api接口
     * @author mabin
     * @date 2024/05/11 09:58
     */
    public void saveApiInterface(ApiInterface apiInterface) {
        if (Objects.isNull(apiInterface)) {
            return;
        }
        if (Objects.isNull(apiInterface.getId())) {
            List<CreateInterfaceResponseItem> interfaceResponseItemList = apiService.createInterface(apiInterface).getData();
            if (CollUtil.isNotEmpty(interfaceResponseItemList)) {
                apiInterface.setId(interfaceResponseItemList.get(0).getId());
            }
        } else {
            apiService.updateInterface(apiInterface);
        }
    }

    /**
     * 导入API数据
     *
     * @param importDataRequest 导入数据请求
     * @return {@link java.lang.Boolean}
     * @author mabin
     * @date 2024/05/31 14:36
     */
    public Boolean importApiInterface(ImportDataRequest importDataRequest) {
        if (Objects.isNull(importDataRequest)) {
            return Boolean.FALSE;
        }
        Response<?> response = apiService.importApiData(importDataRequest);
        return Objects.equals(response.getErrorCode(), Constants.NUM.ZERO);
    }

    /**
     * 获取分类菜单下所有接口
     *
     * @param catId 卡特彼勒id
     * @author mabin
     * @date 2024/05/11 11:00
     */
    public ListInterfaceResponse getCatInterfaces(Integer catId) {
        return apiService.getCatInterfaces(catId).getData();
    }


    /**
     * 组装分类菜单访问地址
     *
     * @param projectId 项目id
     * @param catId     卡特彼勒id
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/11 11:05
     */
    public String genCatUrl(Integer projectId, Integer catId) {
        return format("%s/project/%d/interface/api/cat_%s", url, projectId, catId);
    }


    /**
     * 组装接口访问地址
     *
     * @param projectId 项目id
     * @param id        身份证
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/11 11:06
     */
    public String genInterfaceUrl(Integer projectId, Integer id) {
        return format("%s/project/%d/interface/api/%d", url, projectId, id);
    }


    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }
}

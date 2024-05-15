package easy.api.sdk.yapi;

import com.google.common.reflect.TypeToken;
import easy.api.sdk.yapi.model.*;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class YApiService {

    private static final String TOKEN = "token";

    private final String url;
    private final String token;

    public YApiService(String url, String token) {
        this.url = url;
        this.token = token;
    }

    /**
     * 获取YApi项目信息
     *
     * @return {@link Response <?>}
     * @author mabin
     * @date 2024/05/10 16:36
     */
    public Response<ApiProject> getProject() {
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put(TOKEN, token);
        String response = HttpUtil.doGet(url + "/api/project/get", paramMap);
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<ApiProject>>() {
        }.getType());
    }


    /**
     * 获取YApi项目下所有分类菜单
     *
     * @param projectId 项目id
     * @return {@link Response <java.util.List<easy.api.sdk.yapi.model.ApiCategory>>}
     * @author mabin
     * @date 2024/05/10 17:10
     */
    public Response<List<ApiCategory>> getCategories(Integer projectId) {
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put(TOKEN, token);
        paramMap.put("project_id", Integer.toString(projectId));
        String response = HttpUtil.doGet(url + "/api/interface/getCatMenu", paramMap);
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<List<ApiCategory>>>() {
        }.getType());
    }

    /**
     * 获取YApi项目下单个接口详情
     *
     * @param interfaceId 接口id
     * @return {@link Response <java.lang.Object>}
     * @author mabin
     * @date 2024/05/11 09:39
     */
    public Response<ApiInterface> getApiInterface(Integer interfaceId) {
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put(TOKEN, token);
        paramMap.put("id", Integer.toString(interfaceId));
        String response = HttpUtil.doGet(url + "/api/interface/get", paramMap);
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        Response<ApiInterface> interfaceResponse = JsonUtil.fromJson(response, new TypeToken<Response<ApiInterface>>() {
        }.getType());
        ApiInterface apiInterface = interfaceResponse.getData();
        if (Objects.nonNull(apiInterface) && Objects.isNull(apiInterface.getId())) {
            apiInterface.setId(interfaceId);
        }
        return interfaceResponse;
    }

    /**
     * 创建YApi接口
     *
     * @param apiInterface api接口
     * @return {@link Response <java.util.List<easy.api.sdk.yapi.model.CreateInterfaceResponseItem>>}
     * @author mabin
     * @date 2024/05/11 10:34
     */
    public Response<List<CreateInterfaceResponseItem>> createInterface(ApiInterface apiInterface) {
        if (Objects.isNull(apiInterface)) {
            return Response.fail();
        }
        apiInterface.setToken(token);
        String paramJson = JsonUtil.toJson(apiInterface);
        String response = HttpUtil.doPost(url + "/api/interface/save", paramJson);
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<List<CreateInterfaceResponseItem>>>() {
        }.getType());
    }


    /**
     * 更新YApi接口
     *
     * @param apiInterface api接口
     * @return {@link Response <?>}
     * @author mabin
     * @date 2024/05/11 10:57
     */
    public Response<?> updateInterface(ApiInterface apiInterface) {
        if (Objects.isNull(apiInterface)) {
            return Response.fail();
        }
        apiInterface.setToken(token);
        String response = HttpUtil.doPost(url + "/api/interface/up", JsonUtil.toJson(apiInterface));
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<?>>() {
        }.getType());
    }

    /**
     * 获取分类菜单下所有接口
     *
     * @param catId 分类菜单id
     * @return {@link easy.api.sdk.yapi.model.ListInterfaceResponse}
     * @author mabin
     * @date 2024/05/11 11:01
     */
    public Response<ListInterfaceResponse> getCatInterfaces(Integer catId) {
        if (Objects.isNull(catId)) {
            return Response.fail();
        }
        Map<String, String> paramMap = new HashMap<>(16);
        paramMap.put(TOKEN, token);
        paramMap.put("catid", Integer.toString(catId));
        paramMap.put("page", Integer.toString(1));
        paramMap.put("limit", Integer.toString(1000));
        String response = HttpUtil.doGet(url + "/api/interface/list_cat", paramMap);
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<ListInterfaceResponse>>() {
        }.getType());
    }

    /**
     * 添加YApi类别
     *
     * @param request 请求
     * @return {@link easy.api.sdk.yapi.model.Response<easy.api.sdk.yapi.model.ApiCategory>}
     * @author mabin
     * @date 2024/05/11 11:12
     */
    public Response<ApiCategory> addCategory(CategoryCreateRequest request) {
        if (Objects.isNull(request)) {
            return Response.fail();
        }
        request.setToken(token);
        String response = HttpUtil.doPost(url + "/api/interface/add_cat", JsonUtil.toJson(request));
        if (StringUtils.isBlank(response)) {
            return Response.fail();
        }
        return JsonUtil.fromJson(response, new TypeToken<Response<ApiCategory>>() {
        }.getType());
    }

}

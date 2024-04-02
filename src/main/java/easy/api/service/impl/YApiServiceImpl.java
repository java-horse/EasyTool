package easy.api.service.impl;

import cn.hutool.core.util.URLUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import easy.api.entity.YApiDocInfo;
import easy.api.entity.YApiProjectInfo;
import easy.api.service.YApiService;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * YAPI服务实现
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.service.impl
 * @date 2024/04/01 14:24
 */
public class YApiServiceImpl implements YApiService {

//    String categoryListUrl = "/api/interface/getCatMenu";
//    String addCategoryUrl = "/api/interface/add_cat";

    /**
     * 获取项目信息
     *
     * @param baseUrl 基url
     * @param token   令牌
     * @return {@link YApiProjectInfo }
     * @author mabin
     * @date 2024/04/01 14:23
     */
    @Override
    public YApiProjectInfo getProjectInfo(String baseUrl, String token) {
        if (StringUtils.isAnyBlank(baseUrl, token)) {
            return null;
        }
        HashMap<String, String> paramMap = new HashMap<>(3);
        paramMap.put("token", token);

        String response = HttpUtil.doGet(URLUtil.completeUrl(baseUrl, "/api/project/get"), paramMap);
        return parseData(response, YApiProjectInfo.class);
    }

    /**
     * 保存api(新增或更新API)
     *
     * @param baseUrl     基url
     * @param yApiDocInfo Y API文档信息
     * @return {@link String }
     * @author mabin
     * @date 2024/04/02 16:32
     */
    @Override
    public String saveApi(String baseUrl, YApiDocInfo yApiDocInfo) {
        if (StringUtils.isBlank(baseUrl) || Objects.isNull(yApiDocInfo)) {
            return StringUtils.EMPTY;
        }
        String response = HttpUtil.doPost(URLUtil.completeUrl(baseUrl, "/api/interface/save"), JsonUtil.toJson(yApiDocInfo));
        if (StringUtils.isBlank(response)) {
            return StringUtils.EMPTY;
        }
        JsonObject resObject = JsonUtil.fromObject(response);
        if (Objects.isNull(resObject) || resObject.get("errcode").getAsInt() != 0) {
            return StringUtils.EMPTY;
        }
        return resObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("_id").getAsString();
    }

    /**
     * 解析数据
     *
     * @param response 响应
     * @param clazz    clazz
     * @return {@link T }
     * @author mabin
     * @date 2024/04/01 14:36
     */
    private <T> T parseData(String response, Class<T> clazz) {
        if (StringUtils.isBlank(response)) {
            return null;
        }
        JsonObject resObject = JsonUtil.fromObject(response);
        if (Objects.isNull(resObject) || resObject.get("errcode").getAsInt() != 0) {
            return null;
        }
        JsonElement dataElement = resObject.get("data");
        if (dataElement.isJsonNull()) {
            return null;
        }
        JsonObject dataObject = dataElement.getAsJsonObject();
        if (Objects.isNull(dataObject)) {
            return null;
        }
        return JsonUtil.fromJson(JsonUtil.toJson(dataObject), clazz);
    }

}

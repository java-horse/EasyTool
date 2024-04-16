package easy.api.service;

import easy.api.entity.YApiCategoryInfo;
import easy.api.entity.YApiDocInfo;
import easy.api.entity.YApiProjectInfo;

import java.util.List;

public interface YApiService {

    /**
     * 获取项目信息
     *
     * @param baseUrl 基url
     * @param token   令牌
     * @return {@link easy.api.entity.YApiProjectInfo }
     * @author mabin
     * @date 2024/04/01 14:23
     */
    YApiProjectInfo getProjectInfo(String baseUrl, String token);

    /**
     * 保存api(新增或更新API)
     *
     * @param baseUrl     基url
     * @param yApiDocInfo Y API文档信息
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/02 16:32
     */
    String saveApi(String baseUrl, YApiDocInfo yApiDocInfo);

    /**
     * 新增类别信息
     *
     * @param baseUrl   基url
     * @param token     令牌
     * @param projectId 项目id
     * @param name      名字
     * @param desc      desc
     * @return {@link easy.api.entity.YApiCategoryInfo }
     * @author mabin
     * @date 2024/04/15 13:56
     */
    YApiCategoryInfo addCategoryInfo(String baseUrl, String token, Long projectId, String name, String desc);

    /**
     * 获取类别信息列表
     *
     * @param baseUrl   基url
     * @param token     令牌
     * @param projectId 项目id
     * @return {@link java.util.List<easy.api.entity.YApiCategoryInfo> }
     * @author mabin
     * @date 2024/04/15 13:57
     */
    List<YApiCategoryInfo> listCategoryInfo(String baseUrl, String token, Long projectId);

}

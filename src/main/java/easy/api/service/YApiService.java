package easy.api.service;

import easy.api.entity.YApiDocInfo;
import easy.api.entity.YApiProjectInfo;

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

}

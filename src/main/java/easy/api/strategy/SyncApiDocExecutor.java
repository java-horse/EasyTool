package easy.api.strategy;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import easy.api.context.ApiDocContext;
import easy.api.strategy.impl.ApiFoxSyncApiApiDocStrategy;
import easy.api.strategy.impl.YApiSyncApiApiDocStrategy;
import easy.config.api.ApiDocConfig;
import easy.enums.ApiDocEnum;
import easy.handler.ServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SyncApiDocExecutor {

    private static final Logger log = Logger.getInstance(SyncApiDocExecutor.class);
    private static final Map<ApiDocEnum, SyncApiDocStrategy> STRATEGY_MAP = new ConcurrentHashMap<>(16);

    static {
        STRATEGY_MAP.put(ApiDocEnum.YAPI, ServiceHelper.getService(YApiSyncApiApiDocStrategy.class));
        STRATEGY_MAP.put(ApiDocEnum.API_FOX, ServiceHelper.getService(ApiFoxSyncApiApiDocStrategy.class));
    }


    /**
     * ApiDoc在线同步入口
     *
     * @param apiDocEnum    API文档enum
     * @param apiDocConfig  API文档配置
     * @param apiDocContext API文档上下文
     * @param psiClass      psi类
     * @author mabin
     * @date 2024/04/13 14:41
     */
    public static void syncCore(ApiDocEnum apiDocEnum, ApiDocConfig apiDocConfig, ApiDocContext apiDocContext, PsiClass psiClass) {
        if (Objects.isNull(apiDocEnum)) {
            return;
        }
        try {
            SyncApiDocStrategy syncDocStrategy = STRATEGY_MAP.get(apiDocEnum);
            if (Objects.isNull(syncDocStrategy)) {
                return;
            }
            if (!checkApiDocPlatformConfig(apiDocEnum, apiDocConfig)) {
                return;
            }
            syncDocStrategy.syncApiDoc(apiDocConfig, apiDocContext, psiClass);
        } catch (Exception e) {
            log.error(String.format("在线同步项目接口文档至%s异常: %s", apiDocEnum.getTitle(), e.getMessage()));
        }
    }

    /**
     * 检查API文档平台配置
     *
     * @param apiDocEnum   API文档enum
     * @param apiDocConfig API文档配置
     * @return boolean
     * @author mabin
     * @date 2024/04/13 15:07
     */
    private static boolean checkApiDocPlatformConfig(ApiDocEnum apiDocEnum, ApiDocConfig apiDocConfig) {
        if (apiDocEnum == ApiDocEnum.YAPI) {
            return StringUtils.isNoneBlank(apiDocConfig.getYapiServer(), apiDocConfig.getYapiToken());
        } else if (apiDocEnum == ApiDocEnum.API_FOX) {
            return StringUtils.isNoneBlank(apiDocConfig.getApifoxServer(), apiDocConfig.getApifoxToken());
        }
        return false;
    }

}

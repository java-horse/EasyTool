package easy.api.strategy;

import com.intellij.psi.PsiClass;
import easy.api.strategy.impl.ApiFoxSyncDocStrategy;
import easy.api.strategy.impl.YApiSyncDocStrategy;
import easy.enums.ApiDocEnum;
import easy.handler.ServiceHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncDocExecutor {

    private static final Map<ApiDocEnum, SyncDocStrategy> STRATEGY_MAP = new ConcurrentHashMap<>(16);

    static {
        STRATEGY_MAP.put(ApiDocEnum.YAPI, ServiceHelper.getService(YApiSyncDocStrategy.class));
        STRATEGY_MAP.put(ApiDocEnum.API_FOX, ServiceHelper.getService(ApiFoxSyncDocStrategy.class));
    }

    /**
     * ApiDoc同步统一入口
     *
     * @param apiDocEnum API文档enum
     * @param psiClass   psi类
     * @author mabin
     * @date 2024/04/02 17:00
     */
    public static void sync(ApiDocEnum apiDocEnum, PsiClass psiClass) {

    }

}

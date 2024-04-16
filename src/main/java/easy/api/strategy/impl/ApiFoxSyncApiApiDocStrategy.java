package easy.api.strategy.impl;

import com.intellij.psi.PsiClass;
import easy.api.context.ApiDocContext;
import easy.api.strategy.SyncApiDocStrategy;
import easy.config.api.ApiDocConfig;

public abstract class ApiFoxSyncApiApiDocStrategy extends DefaultSyncApiDocStrategy implements SyncApiDocStrategy {

    /**
     * 同步API文档
     *
     * @param apiDocConfig
     * @param apiDocContext
     * @param psiClass
     * @author mabin
     * @date 2024/04/02 16:52
     */
    @Override
    public void syncApiDoc(ApiDocConfig apiDocConfig, ApiDocContext apiDocContext, PsiClass psiClass) {

    }


}

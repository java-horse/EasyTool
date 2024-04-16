package easy.api.strategy;

import com.intellij.psi.PsiClass;
import easy.api.context.ApiDocContext;
import easy.config.api.ApiDocConfig;

public interface SyncApiDocStrategy {

    /**
     * 同步API文档
     *
     * @author mabin
     * @date 2024/04/02 16:52
     */
    void syncApiDoc(ApiDocConfig apiDocConfig, ApiDocContext apiDocContext, PsiClass psiClass);

}

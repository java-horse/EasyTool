package easy.action.api;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import easy.api.context.ApiDocContext;
import easy.api.strategy.SyncApiDocExecutor;
import easy.enums.ApiDocEnum;

public class ApiDocAction extends AbstractApiDocAction {

    public ApiDocAction(ApiDocEnum apiDocEnum) {
        super(apiDocEnum.getTitle(), apiDocEnum.getDesc(), apiDocEnum.getIcon());
    }

    /**
     * 执行相关动作
     *
     * @param event         事件
     * @param psiClass      psi类
     * @param apiDocContext API文档上下文
     * @author mabin
     * @date 2024/04/13 13:56
     */
    @Override
    protected void execute(AnActionEvent event, PsiClass psiClass, ApiDocContext apiDocContext) {
        String actionText = event.getPresentation().getText();
        SyncApiDocExecutor.syncCore(ApiDocEnum.getEnum(actionText), apiDocConfig, apiDocContext, psiClass);
    }

}

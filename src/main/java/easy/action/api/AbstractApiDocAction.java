package easy.action.api;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.NlsActions;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import easy.api.context.ApiDocContext;
import easy.api.data.ApiDocData;
import easy.api.data.ApiDocDataContent;
import easy.config.api.ApiDocConfig;
import easy.config.api.ApiDocConfigComponent;
import easy.enums.JavaClassTypeEnum;
import easy.handler.ServiceHelper;
import easy.util.PsiClassUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public abstract class AbstractApiDocAction extends AnAction {
    private static final Logger log = Logger.getInstance(AbstractApiDocAction.class);

    protected final ApiDocConfig apiDocConfig = ServiceHelper.getService(ApiDocConfigComponent.class).getState();

    public AbstractApiDocAction() {
    }

    public AbstractApiDocAction(@Nullable @NlsActions.ActionText String text, @Nullable @NlsActions.ActionDescription String description, @Nullable Icon icon) {
        super(text, description, icon);
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
    protected abstract void execute(AnActionEvent event, PsiClass psiClass, ApiDocContext apiDocContext);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiElement targetElement = PsiClassUtil.getTargetElement(anActionEvent);
        if (Objects.isNull(targetElement)) {
            return;
        }
        // 获取当前操作的类
        PsiClass psiClass = PsiClassUtil.getPsiClass(targetElement);
        // 向全局上下文中添加数据
        ApiDocData apiDocData = new ApiDocData();
        apiDocData.setEvent(anActionEvent);
        apiDocData.setModule(ModuleUtil.findModuleForPsiElement(psiClass));
        ApiDocDataContent.setData(apiDocData);
        // 添加当前操作对象数据
        ApiDocContext apiDocContext = new ApiDocContext();
        apiDocContext.setTargetElement(targetElement);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            execute(anActionEvent, psiClass, apiDocContext);
        } catch (Exception e) {
            log.error("【Api Doc】执行业务动作异常", e);
        } finally {
            watch.stop();
            log.info(String.format("【Api Doc】执行业务动作完成, 共耗时: %sms", watch.getStopTime()));
            ApiDocDataContent.remove();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiElement targetElement = PsiClassUtil.getTargetElement(e);
        PsiClass psiClass = PsiClassUtil.getPsiClass(targetElement);
        JavaClassTypeEnum javaClassTypeEnum = JavaClassTypeEnum.get(psiClass);
        if (JavaClassTypeEnum.isNone(javaClassTypeEnum)) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        presentation.setEnabledAndVisible(Objects.nonNull(javaClassTypeEnum) && JavaClassTypeEnum.isController(javaClassTypeEnum));
        super.update(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}

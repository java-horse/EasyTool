package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.handler.SwaggerGenerateHandler;
import easy.util.MessageUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Swagger工具Action
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/11/04 11:14:11
 */
public class SwaggerAction extends AnAction {

    private CommonConfig commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile)) {
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        String selectedText = editor.getSelectionModel().getSelectedText();

        // 二次弹窗确认
        if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmYesCheckBox())) {
            int confirmResult = Messages.showYesNoDialog("Confirm Swagger Generation?", Constants.PLUGIN_NAME, Messages.getQuestionIcon());
            if (MessageConstants.YES == confirmResult) {
                execSwagger(e, project, psiFile, psiClass, selectedText);
            }
        } else if (Boolean.TRUE.equals(commonConfig.getSwaggerConfirmNoCheckBox())) {
            execSwagger(e, project, psiFile, psiClass, selectedText);
        }
    }

    /**
     * 执行swagger处理
     *
     * @param e
     * @param project
     * @param psiFile
     * @param psiClass
     * @param selectedText
     * @return void
     * @author mabin
     * @date 2023/12/16 17:39
     */
    private static void execSwagger(@NotNull AnActionEvent e, Project project, PsiFile psiFile, PsiClass psiClass, String selectedText) {
        new SwaggerGenerateHandler(project, psiFile, psiClass, selectedText).doGenerate();
        MessageUtil.sendActionDingMessage(e);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && Objects.nonNull(psiFile) && Objects.nonNull(psiClass) && editor.getDocument().isWritable());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

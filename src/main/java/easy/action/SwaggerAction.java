package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.handler.SwaggerGenerateHandler;
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
        new SwaggerGenerateHandler(project, psiFile, psiClass, selectedText).doGenerate();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && Objects.nonNull(psiFile) && Objects.nonNull(psiClass));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

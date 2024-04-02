package easy.action.api;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.enums.ApiDocEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ApiDocAction extends AnAction {

    public ApiDocAction(ApiDocEnum apiDocEnum) {
        super(apiDocEnum.getTitle(), apiDocEnum.getDesc(), apiDocEnum.getIcon());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile)) {
            return;
        }
        String actionText = e.getPresentation().getText();
        if (StringUtils.equals(actionText, ApiDocEnum.YAPI.getTitle())) {

        } else if (StringUtils.equals(actionText, ApiDocEnum.API_FOX.getTitle())) {

        }

        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        String selectedText = editor.getSelectionModel().getSelectedText();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(e.getProject()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

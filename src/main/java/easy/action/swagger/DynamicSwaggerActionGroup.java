package easy.action.swagger;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.enums.SwaggerServiceEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DynamicSwaggerActionGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new SwaggerAction(SwaggerServiceEnum.SWAGGER_2.getName(), SwaggerServiceEnum.SWAGGER_2.getIcon()),
                new SwaggerAction(SwaggerServiceEnum.SWAGGER_3.getName(), SwaggerServiceEnum.SWAGGER_3.getIcon())
        };
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

package easy.action.swagger;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SpringAnnotationEnum;
import easy.enums.SwaggerServiceEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicSwaggerActionGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new SwaggerAction(SwaggerServiceEnum.SWAGGER_VIEW.getName(), SwaggerServiceEnum.SWAGGER_VIEW.getIcon(), SwaggerServiceEnum.SWAGGER_VIEW.getKeyboardShortcut()),
                new SwaggerAction(SwaggerServiceEnum.SWAGGER_2.getName(), SwaggerServiceEnum.SWAGGER_2.getIcon(), SwaggerServiceEnum.SWAGGER_2.getKeyboardShortcut()),
                new SwaggerAction(SwaggerServiceEnum.SWAGGER_3.getName(), SwaggerServiceEnum.SWAGGER_3.getIcon(), SwaggerServiceEnum.SWAGGER_3.getKeyboardShortcut())
        };
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (ObjectUtils.anyNull(project, editor, psiFile, psiClass)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        for (PsiAnnotation psiAnnotation : psiClass.getAnnotations()) {
            if (StringUtils.equalsAnyIgnoreCase(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.SERVICE.getName(),
                    SpringAnnotationEnum.COMPONENT.getName(), SpringAnnotationEnum.REPOSITORY.getName(),
                    SpringAnnotationEnum.SPRING_BOOT_APPLICATION.getName(), SpringAnnotationEnum.SPRING_BOOT_TEST.getName(),
                    ExtraPackageNameEnum.RUN_WITH.getName(), SpringAnnotationEnum.CONFIGURATION.getName())) {
                e.getPresentation().setEnabledAndVisible(false);
                return;
            }
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

package easy.action.copy;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.enums.CopyUrlEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * 动态CopyUrl注册Action
 *
 * @project: EasyTool
 * @package: easy.action.copy
 * @author: mabin
 * @date: 2023/12/23 11:16:23
 */
public class DynamicCopyUrlActionGroup extends DefaultActionGroup {

    /**
     * 动态注册Action
     *
     * @param e
     * @return com.intellij.openapi.actionSystem.AnAction[]
     * @author mabin
     * @date 2023/12/23 11:21
     */
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new CopyUrlAction(CopyUrlEnum.COPY_FULL_URL),
                new CopyUrlAction(CopyUrlEnum.COPY_HTTP_URL),
        };
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (ObjectUtils.anyNull(project, editor, psiFile)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (Objects.isNull(psiClass) || Objects.isNull(psiClass.getModifierList())) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiAnnotation[] psiAnnotations = psiClass.getModifierList().getAnnotations();
        e.getPresentation().setEnabledAndVisible(Arrays.stream(psiAnnotations)
                .anyMatch(annotation -> StringUtils.equalsAny(annotation.getQualifiedName(),
                        Constants.SPRING_ANNOTATION.CONTROLLER_ANNOTATION, Constants.SPRING_ANNOTATION.REST_CONTROLLER_ANNOTATION)));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

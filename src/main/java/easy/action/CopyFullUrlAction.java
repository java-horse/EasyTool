package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.handler.CopyFullUrlHandler;
import easy.util.MessageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * 复制全路径URL
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/11/07 13:33:14
 */
public class CopyFullUrlAction extends AnAction {

    private static final Logger log = Logger.getInstance(CopyFullUrlAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getRequiredData(CommonDataKeys.PSI_FILE);
        if (ObjectUtils.anyNull(project, editor, psiFile)) {
            return;
        }
        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        new CopyFullUrlHandler().doCopyFullUrl(psiElement);
        MessageUtil.sendActionDingMessage(e);
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

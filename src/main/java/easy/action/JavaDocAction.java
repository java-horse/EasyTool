package easy.action;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import easy.ui.JavaDocViewDialog;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JavaDocAction extends AnAction {

    private final JavaDocGenerateService JavaDocGenerateService = ApplicationManager.getApplication().getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ApplicationManager.getApplication().getService(JavaDocWriterService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (Objects.isNull(project) || Objects.isNull(editor) || Objects.isNull(psiFile)) {
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (Objects.isNull(psiClass)) {
            return;
        }
        String text = e.getPresentation().getText();
        if (StringUtils.equalsIgnoreCase(text, "JavaDocView")) {
            new JavaDocViewDialog(project, psiClass, psiFile).show();
            return;
        }
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (Objects.isNull(psiElement) || Objects.isNull(psiElement.getNode())) {
            HintManager.getInstance().showErrorHint(editor, "The mouse cursor should be placed on the class name, method name, property name");
            return;
        }
        String comment = JavaDocGenerateService.generate(psiElement);
        if (StringUtils.isBlank(comment)) {
            return;
        }
        javaDocWriterService.writeJavadoc(project, psiElement, comment, Constants.NUM.ZERO);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

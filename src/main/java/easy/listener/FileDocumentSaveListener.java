package easy.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThrowableRunnable;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

public class FileDocumentSaveListener implements FileDocumentManagerListener {
    private final JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private final JavaDocGenerateService JavaDocGenerateService = ServiceHelper.getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ServiceHelper.getService(JavaDocWriterService.class);
    private final Project project;

    public FileDocumentSaveListener(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        if (Objects.isNull(javaDocConfig) || (Boolean.FALSE.equals(javaDocConfig.getAutoGenerateDocClass())
                && Boolean.FALSE.equals(javaDocConfig.getAutoGenerateDocField())
                && Boolean.FALSE.equals(javaDocConfig.getAutoGenerateDocMethod()))) {
           return;
        }
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (Objects.isNull(virtualFile) || !virtualFile.isValid()) {
            return;
        }
        PsiFile psiFile = PsiManagerEx.getInstance(project).findFile(virtualFile);
        if (Objects.isNull(psiFile) || !psiFile.isWritable()) {
            return;
        }
        PsiClass[] psiClasses = PsiTreeUtil.getChildrenOfType(psiFile, PsiClass.class);
        if (Objects.isNull(psiClasses)) {
            return;
        }
        // 事务中提交Document
        WriteCommandAction.runWriteCommandAction(project, () -> PsiDocumentManager.getInstance(project).commitDocument(document));
        // 延迟执行修改操作, 确保不在保存监听器的上下文中运行
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                ThreadUtils.sleep(Duration.ofSeconds(2));
                WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                    for (PsiClass psiClass : psiClasses) {
                        if (Boolean.TRUE.equals(javaDocConfig.getAutoGenerateDocClass()) && Objects.isNull(psiClass.getDocComment())) {
                            generateJavaDoc(psiClass);
                        }
                        PsiMethod[] psiMethods = psiClass.getMethods();
                        if (psiMethods.length != 0 && Boolean.TRUE.equals(javaDocConfig.getAutoGenerateDocMethod())) {
                            for (PsiMethod psiMethod : psiMethods) {
                                if (Objects.nonNull(psiMethod.getDocComment())) {
                                    continue;
                                }
                                generateJavaDoc(psiMethod);
                            }
                        }
                        PsiField[] psiFields = psiClass.getFields();
                        if (psiFields.length != 0 && Boolean.TRUE.equals(javaDocConfig.getAutoGenerateDocField())) {
                            for (PsiField psiField : psiFields) {
                                if (Objects.nonNull(psiField.getDocComment())) {
                                    continue;
                                }
                                generateJavaDoc(psiField);
                            }
                        }
                    }
                });
            } catch (Throwable ignore) {
            }
        });
    }

    /**
     * 生成JavaDoc
     *
     * @param psiElement psi元素
     * @author mabin
     * @date 2024/07/20 17:58
     */
    private void generateJavaDoc(PsiElement psiElement) {
        String comment = JavaDocGenerateService.generate(psiElement);
        if (StringUtils.isNotBlank(comment)) {
            javaDocWriterService.writeJavadoc(project, psiElement, comment, Constants.NUM.ZERO);
        }
    }

}
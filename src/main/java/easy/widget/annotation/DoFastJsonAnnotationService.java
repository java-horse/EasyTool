package easy.widget.annotation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;

public class DoFastJsonAnnotationService extends DoAbstractAnnotationService {


    /**
     * 写入注解
     *
     * @param project     项目
     * @param psiFile     psi文件
     * @param psiElement  psi元素
     * @param elementName 元素名称
     * @author mabin
     * @date 2024/06/21 13:43
     */
    @Override
    protected void writeAnnotation(Project project, PsiFile psiFile, PsiElement psiElement, String elementName) {
        String annotationText = String.format("%s%s(\"%s\")", Constants.AT, ExtraPackageNameEnum.JSON_FIELD.getClassName(), elementName);
        WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.JSON_FIELD.getClassName(), ExtraPackageNameEnum.JSON_FIELD.getName(), annotationText,
                psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
    }

    /**
     * 删除注解
     *
     * @param psiElement psi元素
     * @author mabin
     * @date 2024/06/21 15:26
     */
    @Override
    public void removeAnnotation(PsiElement psiElement) {
        WriteCommandAction.runWriteCommandAction(project, () -> doRemove(ExtraPackageNameEnum.JSON_FIELD.getName(),
                psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
    }

}

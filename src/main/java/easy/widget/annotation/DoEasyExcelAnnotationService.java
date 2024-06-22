package easy.widget.annotation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.StringUtils;

public class DoEasyExcelAnnotationService extends DoAbstractAnnotationService {


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
        if (StringUtils.equalsAny(PsiElementUtil.getPsiElementNameIdentifierText(psiElement), Constants.UID)) {
            WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.EXCEL_IGNORE.getClassName(),
                    ExtraPackageNameEnum.EXCEL_IGNORE.getName(),
                    String.format("%s%s", Constants.AT, ExtraPackageNameEnum.EXCEL_IGNORE.getClassName()),
                    psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.EXCEL_PROPERTY.getClassName(),
                ExtraPackageNameEnum.EXCEL_PROPERTY.getName(),
                String.format("%s%s(\"%s\")", Constants.AT, ExtraPackageNameEnum.EXCEL_PROPERTY.getClassName(), elementName),
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
        if (StringUtils.equalsAny(PsiElementUtil.getPsiElementNameIdentifierText(psiElement), Constants.UID)) {
            WriteCommandAction.runWriteCommandAction(project, () -> doRemove(ExtraPackageNameEnum.EXCEL_IGNORE.getName(),
                    psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> doRemove(ExtraPackageNameEnum.EXCEL_PROPERTY.getName(),
                psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
    }

}

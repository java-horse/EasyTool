package easy.widget.annotation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class DoGsonAnnotationService extends DoAbstractAnnotationService implements DoAnnotationService {


    /**
     * 添加注解
     *
     * @param project     项目
     * @param psiFile     psi文件
     * @param psiElement  psi元素
     * @param elementName 元素名称
     * @author mabin
     * @date 2024/06/19 17:43
     */
    @Override
    public void addAnnotation(Project project, PsiFile psiFile, PsiElement psiElement, String elementName) {
        if (Objects.isNull(project) || Objects.isNull(psiFile) || Objects.isNull(psiElement) || StringUtils.isBlank(elementName)) {
            return;
        }
        this.project = project;
        this.psiFile = psiFile;
        this.elementFactory = JavaPsiFacade.getElementFactory(project);
        String annotationText = String.format("%s%s(\"%s\")", Constants.AT, ExtraPackageNameEnum.SERIALIZED_NAME.getClassName(), elementName);
        WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.SERIALIZED_NAME.getClassName(), ExtraPackageNameEnum.SERIALIZED_NAME.getName(), annotationText,
                psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
    }

}

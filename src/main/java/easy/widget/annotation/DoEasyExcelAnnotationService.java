package easy.widget.annotation;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DoEasyExcelAnnotationService extends DoAbstractAnnotationService {

    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);


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
        if (StringUtils.equalsAny(PsiElementUtil.getPsiElementNameIdentifierText(psiElement), CommonClassNames.SERIAL_VERSION_UID_FIELD_NAME)) {
            WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.EXCEL_IGNORE.getClassName(),
                    ExtraPackageNameEnum.EXCEL_IGNORE.getName(),
                    String.format("%s%s", Constants.AT, ExtraPackageNameEnum.EXCEL_IGNORE.getClassName()),
                    psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
            return;
        }
        // 获取PsiField的说明: JavaDoc -> Swagger2.x -> Swagger3.x -> translate
        if (psiElement instanceof PsiField psiField) {
            PsiDocComment psiDocComment = psiField.getDocComment();
            if (Objects.nonNull(psiDocComment)) {
                PsiElement[] children = psiDocComment.getChildren();
                if (ArrayUtils.isNotEmpty(children)) {
                    String javaDoc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(children));
                    if (StringUtils.isNotBlank(javaDoc)) {
                        elementName = javaDoc;
                    }
                }
            } else {
                String swagger2Value = PsiElementUtil.getAnnotationAttributeValue(psiField.getAnnotation(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage()),
                        List.of(Constants.ANNOTATION_ATTR.VALUE));
                if (StringUtils.isNotBlank(swagger2Value)) {
                    elementName = swagger2Value;
                } else {
                    String swagger3Value = PsiElementUtil.getAnnotationAttributeValue(psiField.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage()),
                            List.of(Constants.ANNOTATION_ATTR.TITLE));
                    if (StringUtils.isNotBlank(swagger3Value)) {
                        elementName = swagger3Value;
                    } else {
                        elementName = translateService.translate(elementName);
                    }
                }
            }
        }
        String finalElementName = elementName;
        WriteCommandAction.runWriteCommandAction(project, () -> doWrite(ExtraPackageNameEnum.EXCEL_PROPERTY.getClassName(),
                ExtraPackageNameEnum.EXCEL_PROPERTY.getName(),
                String.format("%s%s(\"%s\")", Constants.AT, ExtraPackageNameEnum.EXCEL_PROPERTY.getClassName(), finalElementName),
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
        if (StringUtils.equalsAny(PsiElementUtil.getPsiElementNameIdentifierText(psiElement), CommonClassNames.SERIAL_VERSION_UID_FIELD_NAME)) {
            WriteCommandAction.runWriteCommandAction(project, () -> doRemove(ExtraPackageNameEnum.EXCEL_IGNORE.getName(),
                    psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> doRemove(ExtraPackageNameEnum.EXCEL_PROPERTY.getName(),
                psiElement instanceof PsiField ? (PsiField) psiElement : (PsiClass) psiElement));
    }

}

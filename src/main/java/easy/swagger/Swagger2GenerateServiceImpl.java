package easy.swagger;

import com.intellij.psi.*;
import easy.base.Constants;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.PsiElementUtil;
import easy.util.SwaggerCommentUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Swagger2GenerateServiceImpl extends AbstractSwaggerGenerateService {


    /**
     * 生成类注解
     *
     * @param psiClass psi级
     * @author mabin
     * @date 2024/04/22 16:11
     */
    @Override
    protected void genClassAnnotation(PsiClass psiClass) {
        String commentDesc = StringUtils.EMPTY;
        for (PsiElement tmpEle : psiClass.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                commentDesc = SwaggerCommentUtil.getCommentDesc(tmpEle.getText());
                break;
            }
        }
        String attrValue;
        if (isController) {
            attrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.API.getClassPackage()),
                    commentDesc, List.of(Constants.ANNOTATION_ATTR.TAGS));
            if (StringUtils.isNotBlank(attrValue)) {
                attrValue = StringUtils.substringBetween(attrValue, "{", "}");
            }
        } else {
            attrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.API_MODEL.getClassPackage()),
                    commentDesc, List.of(Constants.ANNOTATION_ATTR.VALUE, Constants.ANNOTATION_ATTR.DESCRIPTION));
        }
        if (StringUtils.isBlank(attrValue)) {
            String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
            attrValue = translateService.translate(className);
            if (StringUtils.isBlank(attrValue)) {
                attrValue = isController ? className + SwaggerAnnotationEnum.API.getClassName() : className + SwaggerAnnotationEnum.API_MODEL.getClassName();
            }
        }
        String annotationFromText = isController ? String.format("@%s(tags = {\"%s\"})", SwaggerAnnotationEnum.API.getClassName(), attrValue) : String.format("@%s(\"%s\")", SwaggerAnnotationEnum.API_MODEL.getClassName(), attrValue);
        doWrite(isController ? SwaggerAnnotationEnum.API.getClassName() : SwaggerAnnotationEnum.API_MODEL.getClassName(), isController ? SwaggerAnnotationEnum.API.getClassPackage() : SwaggerAnnotationEnum.API_MODEL.getClassPackage(), annotationFromText, psiClass);
    }

    /**
     * 生成方法注解
     *
     * @param psiMethod psi法
     * @author mabin
     * @date 2024/04/22 16:11
     */
    @Override
    protected void genMethodAnnotation(PsiMethod psiMethod) {

    }

    /**
     * 生成字段注解
     *
     * @param psiField psi场
     * @author mabin
     * @date 2024/04/22 16:12
     */
    @Override
    protected void genFieldAnnotation(PsiField psiField) {

    }
}

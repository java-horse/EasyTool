package easy.swagger;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import easy.base.Constants;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Swagger3GenerateServiceImpl extends AbstractSwaggerGenerateService {

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
        if (Objects.nonNull(psiClass.getDocComment())) {
            commentDesc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(psiClass.getDocComment().getChildren()));
        }
        String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
        if (isController) {
            PsiAnnotation tagAnnotation = psiClass.getAnnotation(SwaggerAnnotationEnum.TAG.getClassPackage());
            String nameAttrValue = PsiElementUtil.getAnnotationAttributeValue(tagAnnotation, commentDesc, List.of(Constants.ANNOTATION_ATTR.NAME));
            if (StringUtils.isBlank(nameAttrValue)) {
                nameAttrValue = translateService.translate(className);
                if (StringUtils.isBlank(nameAttrValue)) {
                    nameAttrValue = className + SwaggerAnnotationEnum.TAG.getClassName();
                }
            }
            String descAttrValue = PsiElementUtil.getAnnotationAttributeValue(tagAnnotation, List.of(Constants.ANNOTATION_ATTR.DESCRIPTION));
            String tagAnnotationText = String.format("%s%s(name = \"%s\", description = \"%s\")", Constants.AT, SwaggerAnnotationEnum.TAG.getClassName(),
                    nameAttrValue, StringUtils.isBlank(descAttrValue) ? className : descAttrValue);
            doWrite(SwaggerAnnotationEnum.TAG.getClassName(), SwaggerAnnotationEnum.TAG.getClassPackage(), tagAnnotationText, psiClass);
        } else {
            String titleAttrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage()), commentDesc, List.of(Constants.ANNOTATION_ATTR.TITLE));
            if (StringUtils.isBlank(titleAttrValue)) {
                titleAttrValue = translateService.translate(className);
                if (StringUtils.isBlank(titleAttrValue)) {
                    titleAttrValue = className + SwaggerAnnotationEnum.SCHEMA.getClassName();
                }
            }
            String schemaAnnotationText = String.format("%s%s(title = \"%s\")", Constants.AT, SwaggerAnnotationEnum.SCHEMA.getClassName(), titleAttrValue);
            doWrite(SwaggerAnnotationEnum.SCHEMA.getClassName(), SwaggerAnnotationEnum.SCHEMA.getClassPackage(), schemaAnnotationText, psiClass);
        }
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

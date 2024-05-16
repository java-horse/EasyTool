package easy.api.parse.util;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import easy.base.Constants;
import easy.enums.SwaggerAnnotationEnum;

/**
 * Swagger解析相关工具
 */
public class PsiSwaggerUtils {

    private PsiSwaggerUtils() {
    }

    public static String getApiCategory(PsiClass psiClass) {
        PsiAnnotation api = PsiAnnotationUtils.getAnnotation(psiClass, SwaggerAnnotationEnum.API.getClassPackage());
        if (api != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(api, Constants.ANNOTATION_ATTR.TAGS);
        }
        PsiAnnotation tag = PsiAnnotationUtils.getAnnotation(psiClass, SwaggerAnnotationEnum.TAG.getClassPackage());
        if (tag != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(tag, Constants.ANNOTATION_ATTR.NAME);
        }
        return null;
    }

    public static String getApiSummary(PsiMethod psiMethod) {
        PsiAnnotation apiOperation = PsiAnnotationUtils.getAnnotation(psiMethod, SwaggerAnnotationEnum.API_OPERATION.getClassPackage());
        if (apiOperation != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiOperation);
        }
        PsiAnnotation operation = PsiAnnotationUtils.getAnnotation(psiMethod, SwaggerAnnotationEnum.OPERATION.getClassPackage());
        if (operation != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(operation, Constants.ANNOTATION_ATTR.SUMMARY);
        }
        return null;
    }

    public static String getParameterDescription(PsiMethod method, PsiParameter psiParameter) {
        PsiAnnotation apiParam = PsiAnnotationUtils.getAnnotation(psiParameter, SwaggerAnnotationEnum.API_PARAM.getClassPackage());
        if (apiParam != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiParam);
        }
        PsiAnnotation parameter = PsiAnnotationUtils.getAnnotation(psiParameter, SwaggerAnnotationEnum.PARAMETER.getClassPackage());
        if (parameter != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(parameter, Constants.ANNOTATION_ATTR.DESCRIPTION);
        }
        PsiAnnotation[] parameters = PsiAnnotationUtils.getAnnotations(psiParameter, SwaggerAnnotationEnum.PARAMETER.getClassPackage());
        for (PsiAnnotation p : parameters) {
            String name = PsiAnnotationUtils.getStringAttributeValueByAnnotation(p, Constants.ANNOTATION_ATTR.NAME);
            if (name != null && name.equals(psiParameter.getName())) {
                return PsiAnnotationUtils.getStringAttributeValueByAnnotation(p, Constants.ANNOTATION_ATTR.DESCRIPTION);
            }
        }
        return null;
    }

    public static String getFieldDescription(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage());
        if (apiModelProperty != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(apiModelProperty);
        }
        PsiAnnotation schema = PsiAnnotationUtils.getAnnotation(psiField, SwaggerAnnotationEnum.SCHEMA.getClassPackage());
        if (schema != null) {
            return PsiAnnotationUtils.getStringAttributeValueByAnnotation(schema, Constants.ANNOTATION_ATTR.DESCRIPTION);
        }
        return null;
    }

    public static boolean isFieldIgnore(PsiField psiField) {
        PsiAnnotation apiModelProperty = PsiAnnotationUtils.getAnnotation(psiField, SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage());
        if (apiModelProperty != null) {
            Boolean hidden = AnnotationUtil.getBooleanAttributeValue(apiModelProperty, Constants.ANNOTATION_ATTR.HIDDEN);
            return Boolean.TRUE.equals(hidden);
        }
        return false;
    }
}

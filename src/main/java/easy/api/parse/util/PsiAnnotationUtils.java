package easy.api.parse.util;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaConstantExpressionEvaluator;
import easy.base.Constants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 注解相关工具类
 */
public class PsiAnnotationUtils {

    private PsiAnnotationUtils() {
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation getAnnotation(PsiModifierListOwner element, String fqn) {
        return element.getAnnotation(fqn);
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation[] getAnnotations(PsiModifierListOwner element, String fqn) {
        return Arrays.stream(element.getAnnotations()).filter(annotation -> fqn.equals(annotation.getQualifiedName())).toArray(PsiAnnotation[]::new);
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation getAnnotation(PsiModifierListOwner element, String... fqnWaits) {
        for (String fqn : fqnWaits) {
            PsiAnnotation annotation = element.getAnnotation(fqn);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取指定注解
     */
    public static PsiAnnotation getAnnotationIncludeExtends(PsiClass element, String fqn) {
        PsiAnnotation annotation = element.getAnnotation(fqn);
        if (annotation == null) {
            for (PsiClassType type : element.getExtendsListTypes()) {
                PsiClass psiClass = type.resolve();
                if (psiClass == null) {
                    continue;
                }
                annotation = psiClass.getAnnotation(fqn);
                if (annotation != null) {
                    break;
                }
            }
        }
        return annotation;
    }

    /**
     * 获取指定元素注解的value属性值
     */
    public static String getStringAttributeValue(PsiModifierListOwner element, String fqn) {
        return getStringAttributeValue(element, fqn, Constants.ANNOTATION_ATTR.VALUE);
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValue(PsiModifierListOwner element, String fqn, String attribute) {
        PsiAnnotation annotation = getAnnotation(element, fqn);
        if (annotation == null) {
            return null;
        }
        return getStringAttributeValueByAnnotation(annotation, attribute);
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValueByAnnotation(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(attribute);
        if (attributeValue == null) {
            return null;
        }
        return getAnnotationMemberValue(attributeValue);
    }

    /**
     * 获取指定元素注解的某个属性值
     */
    public static String getStringAttributeValueByAnnotation(PsiAnnotation annotation) {
        return getStringAttributeValueByAnnotation(annotation, Constants.ANNOTATION_ATTR.VALUE);
    }

    public static List<String> getStringArrayAttribute(PsiModifierListOwner element, String fqn, String attribute) {
        PsiAnnotation annotation = getAnnotation(element, fqn);
        if (annotation == null) {
            return null;
        }
        return getStringArrayAttribute(annotation, attribute);
    }

    public static List<String> getStringArrayAttribute(PsiAnnotation annotation, String attribute) {
        PsiAnnotationMemberValue memberValue = annotation.findAttributeValue(attribute);
        if (memberValue == null) {
            return Collections.emptyList();
        }
        List<String> attributes = Lists.newArrayListWithExpectedSize(1);
        if (memberValue instanceof PsiArrayInitializerMemberValue theMemberValue) {
            PsiAnnotationMemberValue[] values = theMemberValue.getInitializers();
            for (PsiAnnotationMemberValue value : values) {
                String text = getAnnotationMemberValue(value);
                attributes.add(text);
            }
        } else {
            String text = getAnnotationMemberValue(memberValue);
            attributes.add(text);
        }
        return attributes;
    }

    /**
     * 获取注解值
     */
    public static String getAnnotationMemberValue(PsiAnnotationMemberValue memberValue) {
        PsiReference reference = memberValue.getReference();
        if (reference != null) {
            PsiElement resolve = reference.resolve();
            if (resolve instanceof PsiEnumConstant) {
                // 枚举常量
                return ((PsiEnumConstant) resolve).getName();
            } else if (resolve instanceof PsiField) {
                // 引用其他字段
                return PsiFieldUtils.getFieldDefaultValue((PsiField) resolve);
            }
        }
        if (memberValue instanceof PsiClassObjectAccessExpression expression) {
            PsiTypeElement typeElement = expression.getOperand();
            return typeElement.getType().getCanonicalText();
        }

        if (memberValue instanceof PsiExpression) {
            Object constant = JavaConstantExpressionEvaluator
                    .computeConstantExpression((PsiExpression) memberValue, false);
            return constant == null ? null : constant.toString();
        }
        return "";
    }

    public static Integer getIntegerAttributeValueByAnnotation(PsiAnnotation annotation, String attribute) {
        String value = getStringAttributeValueByAnnotation(annotation, attribute);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            // ignored
        }
        return null;
    }

    public static BigDecimal getBigDecimalAttributeValueByAnnotation(PsiAnnotation annotation, String attribute) {
        String value = getStringAttributeValueByAnnotation(annotation, attribute);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            // ignored
        }
        return null;
    }


    /**
     * 获取字段注解值
     */
    public static PsiAnnotation getFieldAnnotation(PsiField psiField, String fqn, boolean includeGetterMethod) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(psiField, fqn);
        if (annotation == null && includeGetterMethod) {
            PsiMethod getterMethod = PsiFieldUtils.getGetterMethod(psiField);
            if (getterMethod != null) {
                annotation = PsiAnnotationUtils.getAnnotation(getterMethod, fqn);
            }
        }
        return annotation;
    }

    /**
     * 获取字段指定注解value属性值
     */
    public static String getFieldAnnotationStringAttributeValue(PsiField psiField, String fqn, String attribute, boolean includeGetterMethod) {
        PsiAnnotation annotation = getFieldAnnotation(psiField, fqn, includeGetterMethod);
        if (annotation != null) {
            return getStringAttributeValueByAnnotation(annotation, attribute);
        }
        return null;
    }
}

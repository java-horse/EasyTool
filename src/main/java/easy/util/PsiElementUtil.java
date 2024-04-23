package easy.util;

import com.intellij.psi.*;
import easy.enums.SpringAnnotationEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class PsiElementUtil {

    private PsiElementUtil() {
    }

    /**
     * 获取PSI元素名称标识符文本
     *
     * @param psiElement psi的元素
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/20 15:36
     */
    public static String getPsiElementNameIdentifierText(PsiElement psiElement) {
        if (Objects.isNull(psiElement)) {
            return StringUtils.EMPTY;
        }
        if (psiElement instanceof PsiClass psiClass) {
            PsiIdentifier nameIdentifier = psiClass.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiMethod psiMethod) {
            PsiIdentifier nameIdentifier = psiMethod.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiField psiField) {
            return psiField.getNameIdentifier().getText();
        } else if (psiElement instanceof PsiParameter psiParameter) {
            PsiIdentifier nameIdentifier = psiParameter.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * PsiClass是否Controller实例
     *
     * @param psiClass psi级
     * @return boolean
     * @author mabin
     * @date 2024/04/22 15:33
     */
    public static boolean isController(PsiClass psiClass) {
        for (PsiAnnotation psiAnnotation : psiClass.getAnnotations()) {
            if (StringUtils.equalsAny(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.CONTROLLER_ANNOTATION.getName(),
                    SpringAnnotationEnum.REST_CONTROLLER_ANNOTATION.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取注解属性值
     *
     * @param psiAnnotation psi注释
     * @param attrNames     属性名称: 获取第一个非空属性值返回
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:56
     */
    public static String getAnnotationAttributeValue(PsiAnnotation psiAnnotation, List<String> attrNames) {
        return getAnnotationAttributeValue(psiAnnotation, StringUtils.EMPTY, attrNames);
    }

    /**
     * 获取注解属性值
     *
     * @param psiAnnotation psi注释
     * @param defaultValue  默认值: 属性值均为空时返回
     * @param attrNames     属性名称: 获取第一个非空属性值返回
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:54
     */
    public static String getAnnotationAttributeValue(PsiAnnotation psiAnnotation, String defaultValue, List<String> attrNames) {
        if (Objects.isNull(psiAnnotation) || Objects.isNull(attrNames) || attrNames.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String attributeValue = StringUtils.EMPTY;
        for (String attrName : attrNames) {
            PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue(attrName);
            if (Objects.isNull(psiAnnotationAttributeValue)) {
                continue;
            }
            attributeValue = washSpecialChar(psiAnnotationAttributeValue.getText());
            if (StringUtils.isNotBlank(attributeValue)) {
                break;
            }
        }
        return StringUtils.isBlank(attributeValue) ? defaultValue : attributeValue;
    }

    /**
     * 清洗特殊字符
     *
     * @param value 价值
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:48
     */
    private static String washSpecialChar(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        value = StringUtils.equals(value, "\"\"") ? StringUtils.EMPTY : value;
        return StringUtils.contains(value, "\"") ? StringUtils.replace(value, "\"", StringUtils.EMPTY) : value;
    }

}

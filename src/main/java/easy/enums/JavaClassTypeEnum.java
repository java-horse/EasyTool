package easy.enums;

import com.intellij.psi.PsiClass;
import easy.util.ApiDocUtil;

import java.util.Objects;

public enum JavaClassTypeEnum {

    CONTROLLER,
    FEIGN,
    INTERFACE,
    OBJECT,
    ENUM,
    ANNOTATION,
    NONE;


    public static JavaClassTypeEnum get(PsiClass psiClass) {
        if (Objects.nonNull(psiClass)) {
            if (ApiDocUtil.isController(psiClass)) {
                return CONTROLLER;
            }
            if(psiClass.isAnnotationType()){
                return ANNOTATION;
            }
            if (psiClass.isInterface()) {
                return INTERFACE;
            }
            if (ApiDocUtil.isFeign(psiClass)) {
                return FEIGN;
            }
            if(psiClass.isEnum()){
                return ENUM;
            }
            if(psiClass.isAnnotationType()){
                return NONE;
            }
            return OBJECT;
        }
        return NONE;
    }

    public static boolean isNone(JavaClassTypeEnum javaClassType) {
        return Objects.isNull(javaClassType) || NONE.equals(javaClassType);
    }

    public static boolean isController(JavaClassTypeEnum javaClassType) {
        return Objects.nonNull(javaClassType) && CONTROLLER.equals(javaClassType);
    }

    public static boolean isInterface(JavaClassTypeEnum javaClassType) {
        return Objects.nonNull(javaClassType) && INTERFACE.equals(javaClassType);
    }

    public static boolean isFeign(JavaClassTypeEnum javaClassType) {
        return Objects.nonNull(javaClassType) && FEIGN.equals(javaClassType);
    }

}

package easy.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.enums.JavaClassTypeEnum;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SpringAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApiDocUtil {

    /**
     * 获取Request注解路径名称列表
     *
     * @return {@link java.util.List<java.lang.String> }
     * @author mabin
     * @date 2024/04/13 14:15
     */
    public static List<String> getMappingQualifiedNameList() {
        return Lists.newArrayList(RequestAnnotationEnum.REQUEST_MAPPING.getQualifiedName(), RequestAnnotationEnum.GET_MAPPING.getQualifiedName(), RequestAnnotationEnum.POST_MAPPING.getQualifiedName(), RequestAnnotationEnum.PUT_MAPPING.getQualifiedName(), RequestAnnotationEnum.DELETE_MAPPING.getQualifiedName(), RequestAnnotationEnum.PATCH_MAPPING.getQualifiedName());
    }

    /**
     * 校验一个方法是否需要解析
     *
     * @param psiClass  类对象
     * @param psiMethod 方法对象
     * @return true 需要解析
     */
    public static boolean isNeedParse(PsiClass psiClass, PsiMethod psiMethod) {
        JavaClassTypeEnum javaClassType = JavaClassTypeEnum.get(psiClass);
        if (JavaClassTypeEnum.isNone(javaClassType)) {
            return false;
        }
        return isValidMethod(javaClassType, psiMethod);
    }


    /**
     * 校验java类是否为Controller
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isController(PsiClass psiClass) {
        return existsAnnotation(psiClass, SpringAnnotationEnum.CONTROLLER_ANNOTATION.getName(), SpringAnnotationEnum.REST_CONTROLLER_ANNOTATION.getName());
    }

    /**
     * 校验java类是否为Dubbo接口
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isDubbo(PsiClass psiClass) {
        return psiClass.isInterface();
    }

    /**
     * 校验java类是否为Feign接口
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isFeign(PsiClass psiClass) {
        return existsAnnotation(psiClass, SpringAnnotationEnum.FEIGN_CLIENT_ANNOTATION.getName());
    }

    private static boolean existsAnnotation(PsiClass psiClass, String... annotationNames) {
        if (Objects.isNull(psiClass) || Objects.isNull(annotationNames) || annotationNames.length <= 0) {
            return false;
        }
        return ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () -> AnnotationUtil.isAnnotated(psiClass, Sets.newHashSet(annotationNames), 0));
    }


    /**
     * 是否为有效的方法(即需要被解析的方法)
     *
     * @param javaClassType Java类类型
     * @param psiMethod     psi的方法
     * @return boolean
     * @author mabin
     * @date 2024/04/13 14:05
     */
    public static boolean isValidMethod(JavaClassTypeEnum javaClassType, PsiMethod psiMethod) {
        return switch (javaClassType) {
            case CONTROLLER -> isControllerMethod(psiMethod);
            case INTERFACE, FEIGN -> true;
            default -> false;
        };
    }


    /**
     * 是否为Controller有效的请求方法
     *
     * @param psiMethod psi的方法
     * @return boolean
     * @author mabin
     * @date 2024/04/13 14:16
     */
    public static boolean isControllerMethod(PsiMethod psiMethod) {
        if (Objects.nonNull(psiMethod)) {
            PsiAnnotation[] annotations = psiMethod.getAnnotations();
            if (annotations.length > 0) {
                for (String mapping : getMappingQualifiedNameList()) {
                    for (PsiAnnotation annotation : annotations) {
                        if (mapping.equals(annotation.getQualifiedName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static List<String> getAllModuleNameList(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        return Arrays.stream(modules).map(Module::getName).collect(Collectors.toList());
    }


    private static String getModulePath(Module module) {
        if (Objects.isNull(module)) {
            return StringUtils.EMPTY;
        }
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if (contentRoots.length > 0) {
            return contentRoots[0].getPath();
        }
        return module.getName();
    }

}

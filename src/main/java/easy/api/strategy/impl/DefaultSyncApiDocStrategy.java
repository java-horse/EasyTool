package easy.api.strategy.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.base.Joiner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ArrayUtil;
import easy.api.strategy.SyncApiDocStrategy;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class DefaultSyncApiDocStrategy implements SyncApiDocStrategy {

    /**
     * 生成API的全局唯一标识
     *
     * @param module    模块
     * @param psiClass  psi类
     * @param psiMethod psi的方法
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 17:56
     */
    protected String genApiKey(Module module, PsiClass psiClass, PsiMethod psiMethod) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        String moduleId = module.getName();
        if (!ArrayUtil.isEmpty(contentRoots)) {
            moduleId = contentRoots[0].getPath();
        }
        return SecureUtil.md5(moduleId + StrUtil.DOT + psiClass.getQualifiedName() + StrUtil.DOT + psiMethod.getName());
    }

    /**
     * 生成API名称
     *
     * @param psiMethod psi的方法
     * @param psiClass  psi类
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 17:59
     */
    protected String genApiTitle(PsiMethod psiMethod, PsiClass psiClass) {
        try {
            return ApplicationManager.getApplication().runReadAction((ThrowableComputable<String, Throwable>) () -> {
                // 获取Swagger的ApiOperation注解中的value属性
                for (PsiAnnotation psiAnnotation : psiMethod.getAnnotations()) {
                    if (!StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.API_OPERATION.getClassPackage())) {
                        continue;
                    }
                    PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue("value");
                    if (Objects.isNull(psiAnnotationAttributeValue)) {
                        continue;
                    }
                    String valueText = psiAnnotationAttributeValue.getText();
                    return StringUtils.contains(valueText, "\"") ? StringUtils.replace(valueText, "\"", StringUtils.EMPTY).trim() : StringUtils.trim(valueText);
                }
                // 获取JavaDoc中第一行非空注释元素即可
                PsiDocComment docComment = psiMethod.getDocComment();
                if (Objects.nonNull(docComment)) {
                    for (PsiElement descriptionElement : docComment.getDescriptionElements()) {
                        String text = descriptionElement.getText().trim();
                        if (StringUtils.isNotEmpty(text)) {
                            return text;
                        }
                    }
                }
                return psiClass.getQualifiedName() + "#" + psiMethod.getName();
            });
        } catch (Throwable e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 生成API类别
     * Swagger -> JavaDoc -> ClassName
     *
     * @param psiClass psi类
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/15 14:24
     */
    protected String genApiCategory(PsiClass psiClass) {
        if (Objects.isNull(psiClass) || Objects.isNull(psiClass.getModifierList())) {
            return StringUtils.EMPTY;
        }
        PsiAnnotation psiAnnotation = psiClass.getModifierList().findAnnotation(SwaggerAnnotationEnum.API.getClassPackage());
        if (Objects.nonNull(psiAnnotation)) {
            PsiAnnotationMemberValue attributeTags = psiAnnotation.findAttributeValue("tags");
            if (Objects.nonNull(attributeTags)) {
                String tagsValue = attributeTags.getText();
                if (StringUtils.isNotBlank(tagsValue) && StringUtils.containsAny(tagsValue, "{", "}")) {
                    tagsValue = StringUtils.substringBetween(tagsValue, "{", "}");
                    if (StringUtils.contains(tagsValue, ",")) {
                        return StringUtils.split(tagsValue)[0];
                    }
                }
            }
            PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue("value");
            if (Objects.nonNull(attributeValue)) {
                return attributeValue.getText();
            }
        }
        for (PsiElement psiElement : psiClass.getChildren()) {
            if (psiElement instanceof PsiDocComment psiDocComment) {
                List<String> descTextList = Arrays.stream(psiDocComment.getDescriptionElements()).map(PsiElement::getText).filter(StringUtils::isNotBlank).map(StringUtils::trim).collect(Collectors.toList());
                return Joiner.on(StringUtils.EMPTY).skipNulls().join(descTextList);
            }
        }
        return psiClass.getName();
    }

    /**
     * 生成API路径
     *
     * @param psiClass  psi类
     * @param psiMethod psi的方法
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/15 15:35
     */
    protected String genApiPath(PsiClass psiClass, PsiMethod psiMethod) {
        if (Objects.isNull(psiClass) || Objects.isNull(psiClass.getModifierList()) || Objects.isNull(psiMethod)) {
            return StringUtils.EMPTY;
        }
        // 获取类路径
        String classPath = StringUtils.EMPTY;
        PsiAnnotation classAnnotation = psiClass.getModifierList().findAnnotation(RequestAnnotationEnum.REQUEST_MAPPING.getQualifiedName());
        if (Objects.nonNull(classAnnotation)) {
            PsiAnnotationMemberValue psiAnnotationAttributeValue = classAnnotation.findAttributeValue("value");
            if (Objects.nonNull(psiAnnotationAttributeValue)) {
                classPath = StringUtils.replace(psiAnnotationAttributeValue.getText(), "\"", StringUtils.EMPTY);
                classPath = StringUtils.startsWith(classPath, "/") ? classPath : "/" + classPath;
                classPath = StringUtils.endsWith(classPath, "/") ? StringUtils.substringBeforeLast(classPath, "/") : classPath;
            }
        }
        // 获取方法路径
        PsiAnnotation[] psiAnnotations = psiMethod.getAnnotations();
        if (ArrayUtils.isNotEmpty(psiAnnotations)) {
            PsiAnnotation methodAnnotation = Arrays.stream(psiAnnotations).filter(annotation -> Objects.nonNull(RequestAnnotationEnum.getEnumByQualifiedName(annotation.getQualifiedName()))).findFirst().orElse(null);
            if (Objects.nonNull(methodAnnotation)) {
                PsiAnnotationMemberValue attributeValue = methodAnnotation.findAttributeValue("value");
                if (Objects.nonNull(attributeValue)) {
                    String methodPath = StringUtils.replace(attributeValue.getText(), "\"", StringUtils.EMPTY);
                    methodPath = StringUtils.startsWith(methodPath, "/") ? methodPath : "/" + methodPath;
                    return classPath + methodPath;
                }
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 生成API方法类型
     *
     * @param psiMethod psi的方法
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/16 10:16
     */
    protected String genApiMethod(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiAnnotation[] psiAnnotations = psiMethod.getAnnotations();
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return StringUtils.EMPTY;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            RequestAnnotationEnum enumByQualifiedName = RequestAnnotationEnum.getEnumByQualifiedName(psiAnnotation.getQualifiedName());
            if (Objects.nonNull(enumByQualifiedName)) {
                if (StringUtils.equals(psiAnnotation.getQualifiedName(), RequestAnnotationEnum.REQUEST_MAPPING.getQualifiedName())) {
                    PsiAnnotationMemberValue methodMemberValue = psiAnnotation.findAttributeValue("method");
                    if (Objects.nonNull(methodMemberValue)) {
                        String name = Arrays.stream(RequestAnnotationEnum.values()).map(RequestAnnotationEnum::getMethodName)
                                .filter(methodName -> StringUtils.equalsIgnoreCase(methodMemberValue.getText(), methodName))
                                .findFirst().orElse(StringUtils.EMPTY);
                        if (StringUtils.isBlank(name)) {
                            return RequestAnnotationEnum.GET_MAPPING.getMethodName();
                        }
                    }
                } else {
                    return enumByQualifiedName.getMethodName();
                }
            }
        }
        return StringUtils.EMPTY;
    }


}

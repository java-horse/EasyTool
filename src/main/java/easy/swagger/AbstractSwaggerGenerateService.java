package easy.swagger;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtil;
import easy.base.Constants;
import easy.enums.*;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import easy.util.MessageUtil;
import easy.util.NotifyUtil;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSwaggerGenerateService implements SwaggerGenerateService {

    protected TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    protected Project project;
    protected PsiFile psiFile;
    protected PsiClass psiClass;
    protected PsiElementFactory elementFactory;
    protected String selectionText;
    protected Boolean isController;
    protected SwaggerServiceEnum serviceEnum;
    /**
     * 当前光标所在PsiElement元素(允许为空)
     */
    protected PsiElement psiElement;

    /**
     * 初始Swagger配置
     *
     * @param project               项目
     * @param psiFile               psi文件
     * @param psiClass              psi级
     * @param selectionText         选择文本
     * @param swaggerAnnotationEnum swagger版本枚举
     * @author mabin
     * @date 2024/04/22 15:14
     */
    @Override
    public void initSwaggerConfig(Project project, PsiFile psiFile, PsiClass psiClass, String selectionText,
                                  SwaggerServiceEnum swaggerAnnotationEnum, @Nullable PsiElement psiElement) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.selectionText = selectionText;
        this.elementFactory = JavaPsiFacade.getElementFactory(project);
        this.isController = PsiElementUtil.isController(psiClass);
        this.serviceEnum = swaggerAnnotationEnum;
        this.psiElement = psiElement;
    }

    /**
     * Swagger注解自动生成
     *
     * @author mabin
     * @date 2024/04/22 14:44
     */
    @Override
    public void doGenerate() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (StringUtils.isNotBlank(selectionText)) {
                genSelection(StringUtils.trim(selectionText));
                return;
            }
            genClassAnnotation(psiClass);
            if (isController) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod psiMethod : methods) {
                    genMethodAnnotation(psiMethod);
                }
                return;
            }
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            for (PsiClass innerClass : innerClasses) {
                genClassAnnotation(innerClass);
                for (PsiField psiField : innerClass.getAllFields()) {
                    genFieldAnnotation(psiField);
                }
            }
            for (PsiField psiField : psiClass.getAllFields()) {
                genFieldAnnotation(psiField);
            }
        });
    }

    /**
     * 生成选中文本指定区域注解
     *
     * @param selectionText 选择文本
     * @author mabin
     * @date 2024/04/22 16:15
     */
    private void genSelection(String selectionText) {
        Map<String, PsiClass> psiClassMap = Maps.newHashMap();
        psiClassMap.put(PsiElementUtil.getPsiElementNameIdentifierText(psiClass), psiClass);
        if (!isController) {
            for (PsiClass innerClass : psiClass.getInnerClasses()) {
                psiClassMap.put(PsiElementUtil.getPsiElementNameIdentifierText(innerClass), innerClass);
            }
        }
        PsiClass selectPsiClass = psiClassMap.get(selectionText);
        if (Objects.nonNull(selectPsiClass)) {
            genClassAnnotation(selectPsiClass);
            return;
        }
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (StringUtils.equals(selectionText, PsiElementUtil.getPsiElementNameIdentifierText(psiMethod))) {
                // 根据参数列表区分同名参数
                if (Objects.nonNull(psiElement) && psiElement instanceof PsiMethod currentPsiMethod) {
                    PsiMethod[] searchPsiMethods = psiClass.findMethodsByName(selectionText, false);
                    if (searchPsiMethods.length >= Constants.NUM.TWO) {
                        for (PsiMethod searchPsiMethod : searchPsiMethods) {
                            if (PsiUtil.allMethodsHaveSameSignature(new PsiMethod[]{currentPsiMethod, searchPsiMethod})) {
                                genMethodAnnotation(currentPsiMethod);
                                return;
                            }
                        }
                    }
                }
                genMethodAnnotation(psiMethod);
                return;
            }
        }
        List<PsiField> psiFieldList = Lists.newArrayList(psiClass.getAllFields());
        Arrays.stream(psiClass.getInnerClasses()).forEach(innerClass -> psiFieldList.addAll(Arrays.asList(innerClass.getAllFields())));
        for (PsiField psiField : psiFieldList) {
            if (StringUtils.equals(selectionText, PsiElementUtil.getPsiElementNameIdentifierText(psiField))) {
                genFieldAnnotation(psiField);
                return;
            }
        }
        NotifyUtil.notify("鼠标光标应选中或放置在类名，方法名，字段名上!");
    }

    /**
     * 写入注解文本
     *
     * @param name                 名称
     * @param qualifiedName        限定名称
     * @param annotationText       注释文本
     * @param psiModifierListOwner psi修改器列表所有者
     * @author mabin
     * @date 2024/04/23 14:34
     */
    protected void doWrite(String name, String qualifiedName, String annotationText, PsiModifierListOwner psiModifierListOwner) {
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (Objects.isNull(modifierList)) {
            return;
        }
        PsiAnnotation existAnnotation = modifierList.findAnnotation(qualifiedName);
        if (Objects.nonNull(existAnnotation)) {
            existAnnotation.delete();
        }
        PsiNameValuePair[] attributes = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner).getParameterList().getAttributes();
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = modifierList.addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 获取http方法类型
     *
     * @param psiAnnotations psi注释
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 13:37
     */
    protected String getHttpMethodName(PsiAnnotation[] psiAnnotations) {
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return StringUtils.EMPTY;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            RequestAnnotationEnum requestAnnotationEnum = RequestAnnotationEnum.getEnumByQualifiedName(psiAnnotation.getQualifiedName());
            if (Objects.isNull(requestAnnotationEnum)) {
                continue;
            }
            if (Objects.equals(requestAnnotationEnum, RequestAnnotationEnum.REQUEST_MAPPING)) {
                String attributeValue = PsiElementUtil.getAnnotationAttributeValue(psiAnnotation, List.of(Constants.ANNOTATION_ATTR.METHOD));
                return StringUtils.isNotBlank(attributeValue) ? attributeValue : requestAnnotationEnum.getMethodName();
            }
            return requestAnnotationEnum.getMethodName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取参数数据类型
     *
     * @param psiParameter psi参数
     * @return {@link String}
     * @author mabin
     * @date 2024/04/24 14:10
     */
    protected String getDataType(PsiParameter psiParameter) {
        PsiType psiType = psiParameter.getType();
        String baseType = BaseTypeEnum.findBaseType(psiType.getCanonicalText());
        if (StringUtils.isNotBlank(baseType)) {
            return baseType;
        }
        if (Boolean.TRUE.equals(BaseTypeEnum.isBaseType(psiType.getCanonicalText()))) {
            return psiType.getCanonicalText();
        }
        if (StringUtils.equalsAny(psiType.getCanonicalText(), ExtraPackageNameEnum.MULTIPART_FILE.getName(), File.class.getName())) {
            return "file";
        }
        for (PsiType superType : psiType.getSuperTypes()) {
            if (StringUtils.equalsAny(superType.getCanonicalText(), ExtraPackageNameEnum.MULTIPART_FILE.getName(), File.class.getName())) {
                return "file";
            }
        }
        return psiType.getPresentableText();
    }

    /**
     * 获取参数类型
     *
     * @param psiParameter psi参数
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 14:29
     */
    protected String getParamType(PsiParameter psiParameter) {
        PsiAnnotation[] psiAnnotations = psiParameter.getAnnotations();
        String paramType = StringUtils.EMPTY;
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            switch (serviceEnum) {
                case SWAGGER_2 -> paramType = SpringAnnotationEnum.REQUEST_PARAM_TEXT.getParamType();
                case SWAGGER_3 -> paramType = SpringAnnotationEnum.REQUEST_PARAM_TEXT.getParamIn();
            }
            return paramType;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            String qualifiedName = psiAnnotation.getQualifiedName();
            if (StringUtils.isBlank(qualifiedName)) {
                continue;
            }
            SpringAnnotationEnum annotationEnum = SpringAnnotationEnum.getEnum(qualifiedName);
            if (Objects.nonNull(annotationEnum)) {
                switch (serviceEnum) {
                    case SWAGGER_2 -> paramType = annotationEnum.getParamType();
                    case SWAGGER_3 -> paramType = annotationEnum.getParamIn();
                }
            }
        }
        if (StringUtils.equals(getDataType(psiParameter), "file")) {
            paramType = "form";
            switch (serviceEnum) {
                case SWAGGER_2 -> paramType = "form";
                case SWAGGER_3 -> paramType = StringUtils.EMPTY;
            }
        }
        return paramType;
    }

    /**
     * 获取参数是否必填属性
     *
     * @param psiParameter psi参数
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 14:34
     */
    protected String getParamRequired(PsiParameter psiParameter) {
        PsiAnnotation[] psiAnnotations = psiParameter.getAnnotations();
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return StringUtils.EMPTY;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            String requireValue = PsiElementUtil.getAnnotationAttributeValue(psiAnnotation, StringUtils.EMPTY, List.of(Constants.ANNOTATION_ATTR.REQUIRED));
            if (StringUtils.isNotBlank(requireValue)) {
                return requireValue;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取类@Api中或者@Tag的标签属性值
     *
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/26 09:31
     */
    protected String getClassTagAttrValue() {
        String tagValue;
        switch (serviceEnum) {
            case SWAGGER_2 ->
                    tagValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.API.getClassPackage()), List.of(Constants.ANNOTATION_ATTR.TAGS));
            case SWAGGER_3 ->
                    tagValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.TAG.getClassPackage()), List.of(Constants.ANNOTATION_ATTR.NAME));
            default -> tagValue = StringUtils.EMPTY;
        }
        if (StringUtils.isNotBlank(tagValue) && StringUtils.containsAny(tagValue, "{", "}")) {
            tagValue = StringUtils.substringBetween(tagValue, "{", "}");
        }
        return tagValue;
    }

    /**
     * 导入依赖
     *
     * @param className 类名
     * @author mabin
     * @date 2024/04/27 16:16
     */
    protected void addImport(String className) {
        addImport(elementFactory, psiFile, className);
    }

    /**
     * 导入依赖
     *
     * @param elementFactory 元件厂
     * @param file           锉
     * @param className      类名
     * @author mabin
     * @date 2024/04/23 14:37
     */
    protected void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile javaFile)) {
            return;
        }
        PsiImportList importList = javaFile.getImportList();
        if (Objects.isNull(importList)) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        // 待导入类有多个同名类或没有时 让用户自行处理
        if (psiClasses.length != 1) {
            return;
        }
        PsiClass waiteImportClass = psiClasses[0];
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            PsiJavaCodeReferenceElement importReference = is.getImportReference();
            if (Objects.isNull(importReference)) {
                continue;
            }
            if (StringUtils.equals(waiteImportClass.getQualifiedName(), importReference.getQualifiedName())) {
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }

    /**
     * 获取验证程序限制文本
     *
     * @param psiField psi场
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 15:26
     */
    protected String getValidatorLimitText(PsiField psiField) {
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return StringUtils.EMPTY;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            String qualifiedName = psiAnnotation.getQualifiedName();
            if (StringUtils.equalsAny(qualifiedName, ExtraPackageNameEnum.SIZE.getName(), ExtraPackageNameEnum.LENGTH.getName())) {
                String validatorText = StringUtils.EMPTY;
                PsiAnnotationMemberValue minMember = psiAnnotation.findAttributeValue(Constants.ANNOTATION_ATTR.MIN);
                PsiAnnotationMemberValue maxMember = psiAnnotation.findAttributeValue(Constants.ANNOTATION_ATTR.MAX);
                if (Objects.equals(SwaggerServiceEnum.SWAGGER_2, serviceEnum)) {
                    if (Objects.nonNull(minMember)) {
                        validatorText = Constants.ANNOTATION_ATTR.MIN + "=" + minMember.getText() + StrUtil.COMMA;
                    }
                    if (Objects.nonNull(maxMember)) {
                        validatorText += Constants.ANNOTATION_ATTR.MAX + "=" + maxMember.getText();
                    }
                    validatorText = StringUtils.endsWith(validatorText, StrUtil.COMMA) ? validatorText.substring(0, validatorText.length() - 1) : validatorText;
                    return "(" + validatorText + ")";
                } else if (Objects.equals(SwaggerServiceEnum.SWAGGER_3, serviceEnum)) {
                    StringBuilder validBuilder = new StringBuilder(", ");
                    if (Objects.nonNull(minMember)) {
                        if (StringUtils.equals(qualifiedName, ExtraPackageNameEnum.SIZE.getName())) {
                            validBuilder.append("minimum = \"").append(minMember.getText()).append("\"");
                        } else if (StringUtils.equals(qualifiedName, ExtraPackageNameEnum.LENGTH.getName())) {
                            validBuilder.append(", minLength = ").append(minMember.getText());
                        }
                    }
                    if (Objects.nonNull(maxMember)) {
                        if (StringUtils.equals(qualifiedName, ExtraPackageNameEnum.SIZE.getName())) {
                            validBuilder.append("maximum = \"").append(maxMember.getText()).append("\"");
                        } else if (StringUtils.equals(qualifiedName, ExtraPackageNameEnum.LENGTH.getName())) {
                            validBuilder.append(", maxLength = ").append(maxMember.getText());
                        }
                    }
                    return validBuilder.toString();
                }
            }
        }
        return StringUtils.EMPTY;
    }


    /**
     * 属性上是否有必填校验注解(EasyTool专属定制逻辑)
     *
     * @param psiField
     * @return boolean
     * @author mabin
     * @date 2023/11/4 15:51
     */
    protected boolean isValidate(PsiField psiField) {
        boolean valid = false;
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return valid;
        }
        for (PsiAnnotation annotation : psiAnnotations) {
            String qualifiedName = annotation.getQualifiedName();
            if (StringUtils.startsWith(qualifiedName, "javax.validation.constraints") && !StringUtils.equals(qualifiedName, ExtraPackageNameEnum.NULL.getName()) && !StringUtils.equals(qualifiedName, ExtraPackageNameEnum.SIZE.getName())) {
                valid = true;
                break;
            }
            if (StringUtils.equalsAny(qualifiedName, ExtraPackageNameEnum.SIZE.getName(), ExtraPackageNameEnum.LENGTH.getName())) {
                PsiAnnotationMemberValue minMember = annotation.findAttributeValue(Constants.ANNOTATION_ATTR.MIN);
                if (Objects.nonNull(minMember) && Integer.parseInt(minMember.getText()) > 0) {
                    valid = true;
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * 元素是否@Deprecated注解
     *
     * @param psiModifierListOwner psi修改器列表所有者
     * @return boolean
     * @author mabin
     * @date 2024/04/30 14:32
     */
    protected boolean isDeprecated(PsiModifierListOwner psiModifierListOwner) {
        PsiAnnotation[] psiAnnotations = psiModifierListOwner.getAnnotations();
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return false;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (StringUtils.equals(psiAnnotation.getQualifiedName(), Deprecated.class.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成类注解
     *
     * @param psiClass psi级
     * @author mabin
     * @date 2024/04/22 16:11
     */
    protected abstract void genClassAnnotation(PsiClass psiClass);

    /**
     * 生成方法注解
     *
     * @param psiMethod psi法
     * @author mabin
     * @date 2024/04/22 16:11
     */
    protected abstract void genMethodAnnotation(PsiMethod psiMethod);

    /**
     * 生成字段注解
     *
     * @param psiField psi场
     * @author mabin
     * @date 2024/04/22 16:12
     */
    protected abstract void genFieldAnnotation(PsiField psiField);

}

package easy.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import easy.base.Constants;
import easy.enums.BaseTypeEnum;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SpringAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.service.TranslateService;
import easy.util.LanguageUtil;
import easy.util.SwaggerCommentUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Swagger生成处理
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/04 10:23:07
 */
public class SwaggerGenerateHandler {

    private static final Logger log = Logger.getInstance(SwaggerGenerateHandler.class);
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final PsiElementFactory elementFactory;
    private final String selectionText;

    public SwaggerGenerateHandler(Project project, PsiFile psiFile, PsiClass psiClass, String selectionText) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.selectionText = selectionText;
        this.elementFactory = JavaPsiFacade.getElementFactory(project);
    }

    /**
     * 自动生成Swagger2.x注解处理
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/11/4 14:59
     */
    public void doGenerate() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            boolean isController = this.isController(psiClass);
            if (StringUtils.isNotBlank(selectionText)) {
                this.generateSelection(psiClass, selectionText, isController);
                return;
            }
            this.generateClassAnnotation(psiClass, isController);
            if (isController) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod psiMethod : methods) {
                    this.generateMethodAnnotation(psiMethod);
                }
            } else {
                PsiClass[] innerClasses = psiClass.getInnerClasses();
                for (PsiClass innerClass : innerClasses) {
                    this.generateClassAnnotation(innerClass, false);
                    for (PsiField psiField : innerClass.getAllFields()) {
                        this.generateFieldAnnotation(psiField);
                    }
                }
                for (PsiField psiField : psiClass.getAllFields()) {
                    this.generateFieldAnnotation(psiField);
                }
            }
        });
    }

    /**
     * 类是否为controller
     *
     * @param psiClass 类元素
     * @return boolean
     */
    private void generateSelection(PsiClass psiClass, String selectionText, boolean isController) {
        Map<String, PsiClass> psiClassMap = Maps.newHashMap();
        psiClassMap.put(psiClass.getNameIdentifier().getText(), psiClass);
        if (!isController) {
            for (PsiClass innerClass : psiClass.getInnerClasses()) {
                psiClassMap.put(innerClass.getNameIdentifier().getText(), innerClass);
            }
        }
        if (psiClassMap.containsKey(selectionText)) {
            this.generateClassAnnotation(psiClassMap.get(selectionText), isController);
            return;
        }
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod psiMethod : methods) {
            if (StringUtils.equals(selectionText, psiMethod.getNameIdentifier().getText())) {
                this.generateMethodAnnotation(psiMethod);
                return;
            }
        }
        List<PsiField> psiFieldList = Lists.newArrayList(psiClass.getAllFields());
        Arrays.stream(psiClass.getInnerClasses()).forEach(innerClass -> psiFieldList.addAll(Arrays.asList(innerClass.getAllFields())));
        for (PsiField psiField : psiFieldList) {
            if (StringUtils.equals(selectionText, psiField.getNameIdentifier().getText())) {
                this.generateFieldAnnotation(psiField);
                return;
            }
        }
    }

    /**
     * 生成类注解
     *
     * @param psiClass
     * @param isController
     * @return void
     * @author mabin
     * @date 2023/11/24 10:14
     */
    private void generateClassAnnotation(PsiClass psiClass, boolean isController) {
        String commentDesc = null;
        for (PsiElement tmpEle : psiClass.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                commentDesc = SwaggerCommentUtil.getCommentDesc(tmpEle.getText());
            }
        }
        String attrValue;
        if (isController) {
            PsiAnnotation apiExist = psiClass.getModifierList().findAnnotation(SwaggerAnnotationEnum.API.getClassPackage());
            attrValue = this.getAttribute(apiExist, "tags", commentDesc);
            if (StringUtils.isNotBlank(attrValue) && StringUtils.containsAny(attrValue, "{", "}")) {
                attrValue = StringUtils.substringBetween(attrValue, "{", "}");
            }
        } else {
            PsiAnnotation apiModelExist = psiClass.getModifierList().findAnnotation(SwaggerAnnotationEnum.API_MODEL.getClassPackage());
            attrValue = this.getAttribute(apiModelExist, "description", commentDesc);
        }
        attrValue = washSpecialChar(attrValue);
        if (StringUtils.isBlank(attrValue)) {
            String className = psiClass.getNameIdentifier().getText();
            attrValue = translateService.translate(className);
            if (StringUtils.isBlank(attrValue)) {
                attrValue = isController ? className + SwaggerAnnotationEnum.API.getClassName() : className + SwaggerAnnotationEnum.API_MODEL.getClassName();
            }
        }
        String annotationFromText = isController ? String.format("@%s(tags = {\"%s\"})", SwaggerAnnotationEnum.API.getClassName(), attrValue)
                : String.format("@%s(description = \"%s\")", SwaggerAnnotationEnum.API_MODEL.getClassName(), attrValue);
        this.doWrite(isController ? SwaggerAnnotationEnum.API.getClassName() : SwaggerAnnotationEnum.API_MODEL.getClassName(),
                isController ? SwaggerAnnotationEnum.API.getClassPackage() : SwaggerAnnotationEnum.API_MODEL.getClassPackage(),
                annotationFromText, psiClass);
    }

    /**
     * 生成方法注解
     *
     * @param psiMethod
     * @return void
     * @author mabin
     * @date 2023/11/4 16:47
     */
    private void generateMethodAnnotation(PsiMethod psiMethod) {
        String commentDesc = StringUtils.EMPTY;
        Map<String, String> methodParamCommentDesc = null;
        for (PsiElement tmpEle : psiMethod.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                PsiComment classComment = (PsiComment) tmpEle;
                String tmpText = classComment.getText();
                methodParamCommentDesc = SwaggerCommentUtil.getCommentMethodParam(tmpText);
                commentDesc = SwaggerCommentUtil.getCommentDesc(tmpText);
            }
        }
        // 如果存在注解，获取注解原本的value和notes内容
        PsiAnnotation apiOperationExist = psiMethod.getModifierList().findAnnotation(SwaggerAnnotationEnum.API_OPERATION.getClassPackage());
        String apiOperationAttrValue = this.getAttribute(apiOperationExist, "value", commentDesc);
        apiOperationAttrValue = washSpecialChar(apiOperationAttrValue);
        String apiOperationAttrNotes = this.getAttribute(apiOperationExist, "notes", commentDesc);
        apiOperationAttrNotes = washSpecialChar(apiOperationAttrNotes);
        // 如果注解和注释都不存在, 尝试自动翻译方法名作为value值
        if (StringUtils.isBlank(apiOperationAttrValue)) {
            apiOperationAttrValue = translateService.translate(psiMethod.getNameIdentifier().getText());
        }
        PsiAnnotation[] psiMethodAnnotations = psiMethod.getModifierList().getAnnotations();
        String methodValue = this.getMappingAttribute(psiMethodAnnotations, "method");
        StringBuilder apiOperationAnnotationText = new StringBuilder();
        if (StringUtils.isNotBlank(methodValue)) {
            apiOperationAnnotationText.append(Constants.AT).append(SwaggerAnnotationEnum.API_OPERATION.getClassName())
                    .append("(value = ").append("\"").append(apiOperationAttrValue).append("\"");
            if (StringUtils.isNotBlank(apiOperationAttrNotes)) {
                apiOperationAnnotationText.append(", notes = ").append("\"").append(apiOperationAttrNotes).append("\"");
            }
            apiOperationAnnotationText.append(", ").append("httpMethod = ").append("\"").append(methodValue).append("\"").append(")");
        } else {
            apiOperationAnnotationText.append(Constants.AT).append(SwaggerAnnotationEnum.API_OPERATION.getClassName())
                    .append("(value = ").append("\"").append(apiOperationAttrValue).append("\"");
            if (StringUtils.isNotBlank(apiOperationAttrNotes)) {
                apiOperationAnnotationText.append(", notes = ").append("\"").append(apiOperationAttrNotes).append("\"");
            }
            apiOperationAnnotationText.append(")");
        }

        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        List<String> apiImplicitParamList = new ArrayList<>(psiParameters.length);
        for (PsiParameter psiParameter : psiParameters) {
            PsiType psiType = psiParameter.getType();
            String paramType = "query";
            String required = null;
            // 忽略生成 @ApiIgnore 注解的参数
            PsiAnnotation[] psiParameterAnnotations = psiParameter.getModifierList().getAnnotations();
            if (Arrays.stream(psiParameterAnnotations).anyMatch(psiAnnotation -> StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.API_IGNORE.getClassPackage()))) {
                continue;
            }
            for (PsiAnnotation psiAnnotation : psiParameterAnnotations) {
                String qualifiedName = psiAnnotation.getQualifiedName();
                if (StringUtils.isBlank(qualifiedName)) {
                    break;
                }
                SpringAnnotationEnum annotationEnum = SpringAnnotationEnum.getEnum(qualifiedName);
                if (Objects.nonNull(annotationEnum)) {
                    switch (annotationEnum) {
                        case REQUEST_HEADER_TEXT:
                            paramType = "header";
                            break;
                        case REQUEST_PARAM_TEXT:
                            paramType = "query";
                            break;
                        case PATH_VARIABLE_TEXT:
                            paramType = "path";
                            break;
                        case REQUEST_BODY_TEXT:
                            paramType = "body";
                            break;
                        default:
                            break;
                    }
                }
                required = this.getAttribute(psiAnnotation, "required", StringUtils.EMPTY);
            }
            String dataType = SwaggerCommentUtil.getDataType(psiType.getCanonicalText(), psiType);
            if (StringUtils.equals(dataType, "file")) {
                paramType = "form";
            }
            String paramDesc = StringUtils.EMPTY;
            String paramName = psiParameter.getNameIdentifier().getText();
            if (methodParamCommentDesc != null) {
                paramDesc = methodParamCommentDesc.get(paramName);
            }
            StringBuilder apiImplicitParamText = new StringBuilder();
            apiImplicitParamText.append(Constants.AT).append(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassName())
                    .append("(paramType = ").append("\"").append(paramType).append("\"")
                    .append(", name = ").append("\"").append(paramName).append("\"");
            if (StringUtils.isBlank(paramDesc)) {
                paramDesc = translateService.translate(paramName);
            }
            if (StringUtils.isNotBlank(paramDesc) && LanguageUtil.isContainsChinese(paramDesc)) {
                apiImplicitParamText.append(", value = ").append("\"").append(paramDesc).append("\"");
            }
            if (Boolean.TRUE.equals(BaseTypeEnum.isBaseType(dataType)) || StringUtils.equalsAny(dataType, "file")) {
                apiImplicitParamText.append(", dataType = ").append("\"").append(dataType).append("\"");
            } else {
                apiImplicitParamText.append(", dataTypeClass = ");
                if (StringUtils.containsAny(dataType, "<", ">")) {
                    if (!StringUtils.containsAnyIgnoreCase(dataType, "map")) {
                        String collDataType = StringUtils.substringBetween(dataType, "<", ">");
                        apiImplicitParamText.append(collDataType).append(".class");
                    } else {
                        apiImplicitParamText.append("Map.class");
                    }
                    apiImplicitParamText.append(", allowMultiple = true");
                } else if (StringUtils.containsAny(dataType, "[", "]")) {
                    String collDataType = StringUtils.substringBefore(dataType, "[");
                    apiImplicitParamText.append(collDataType).append(".class").append(", allowMultiple = true");
                } else {
                    apiImplicitParamText.append(dataType).append(".class");
                }
            }
            if (StringUtils.equals(required, "true")) {
                apiImplicitParamText.append(", required = true");
            }
            apiImplicitParamText.append(")");
            apiImplicitParamList.add(apiImplicitParamText.toString());
        }

        boolean complex = false;
        String apiImplicitParamsAnnotationText;
        if (CollectionUtils.isNotEmpty(apiImplicitParamList) && apiImplicitParamList.size() == 1) {
            apiImplicitParamsAnnotationText = apiImplicitParamList.get(0);
        } else {
            apiImplicitParamsAnnotationText = apiImplicitParamList.stream().collect(Collectors.joining(",\n", Constants.AT + SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassName() + "({\n", "\n})"));
            complex = true;
        }

        this.doWrite(SwaggerAnnotationEnum.API_OPERATION.getClassName(), SwaggerAnnotationEnum.API_OPERATION.getClassPackage(), apiOperationAnnotationText.toString(), psiMethod);
        if (StringUtils.isNotEmpty(apiImplicitParamsAnnotationText)) {
            if (complex) {
                this.doWrite(SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassName(), SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassPackage(), apiImplicitParamsAnnotationText, psiMethod);
            } else {
                this.doWrite(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassName(), SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassPackage(), apiImplicitParamsAnnotationText, psiMethod);
            }
        }
    }

    /**
     * 生成属性注解
     *
     * @param psiField
     * @return void
     * @author mabin
     * @date 2023/11/24 9:24
     */
    private void generateFieldAnnotation(PsiField psiField) {
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        String fieldName = psiField.getNameIdentifier().getText();
        StringBuilder apiModelPropertyText = new StringBuilder().append(Constants.AT).append(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassName());
        if (StringUtils.equals(fieldName, Constants.UID)) {
            apiModelPropertyText.append("(hidden = true)");
        } else {
            String validatorText = getValidatorText(psiAnnotations);
            String commentDesc = null;
            for (PsiElement tmpEle : psiField.getChildren()) {
                if (tmpEle instanceof PsiComment) {
                    commentDesc = SwaggerCommentUtil.getCommentDesc(tmpEle.getText());
                }
            }
            PsiAnnotation apiModelPropertyExist = psiField.getModifierList().findAnnotation(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage());
            String apiModelPropertyAttrValue = this.getAttribute(apiModelPropertyExist, "value", commentDesc);
            apiModelPropertyAttrValue = washSpecialChar(apiModelPropertyAttrValue);
            String apiModelPropertyAttrNotes = this.getAttribute(apiModelPropertyExist, "notes", StringUtils.EMPTY);
            apiModelPropertyAttrNotes = washSpecialChar(apiModelPropertyAttrNotes);
            if (StringUtils.isBlank(apiModelPropertyAttrValue)) {
                apiModelPropertyAttrValue = translateService.translate(fieldName);
                if (StringUtils.isBlank(apiModelPropertyAttrValue)) {
                    apiModelPropertyAttrValue = fieldName;
                }
            }
            if (StringUtils.isBlank(apiModelPropertyAttrNotes)) {
                if (StringUtils.isNotBlank(validatorText)) {
                    apiModelPropertyAttrNotes = validatorText;
                }
            } else if (StringUtils.isNotBlank(validatorText) && !StringUtils.contains(apiModelPropertyAttrNotes, validatorText)) {
                apiModelPropertyAttrNotes += ", " + validatorText;
            }
            apiModelPropertyText.append("(value=\"").append(apiModelPropertyAttrValue).append("\"");
            if (StringUtils.isNotBlank(apiModelPropertyAttrNotes)) {
                apiModelPropertyText.append(", notes=\"").append(apiModelPropertyAttrNotes).append("\"");
            }
            apiModelPropertyText.append(isValidate(psiAnnotations) ? ", required=true)" : ")");
        }
        this.doWrite(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassName(), SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage(), apiModelPropertyText.toString(), psiField);
    }

    /**
     * 属性上是否有必填校验注解
     *
     * @param psiAnnotation
     * @return boolean
     * @author mabin
     * @date 2023/11/4 15:51
     */
    private boolean isValidate(PsiAnnotation[] psiAnnotation) {
        boolean valid = false;
        if (Objects.isNull(psiAnnotation)) {
            return valid;
        }
        for (PsiAnnotation annotation : psiAnnotation) {
            String qualifiedName = annotation.getQualifiedName();
            if (StringUtils.startsWith(qualifiedName, "javax.validation.constraints")
                    && !StringUtils.equals(qualifiedName, "javax.validation.constraints.Null")
                    && !StringUtils.equals(qualifiedName, "javax.validation.constraints.Size")) {
                valid = true;
                break;
            }
            if (StringUtils.equalsAny(qualifiedName, "javax.validation.constraints.Size", "org.hibernate.validator.constraints.Length")) {
                PsiAnnotationMemberValue minMember = annotation.findAttributeValue("min");
                if (Objects.nonNull(minMember) && Integer.parseInt(minMember.getText()) > 0) {
                    valid = true;
                    break;
                }
            }
        }
        return valid;
    }

    /**
     * 尝试获取属性上的@Size和@Length校验注解限制信息
     *
     * @param psiAnnotations
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/18 16:54
     */
    private String getValidatorText(PsiAnnotation[] psiAnnotations) {
        if (Objects.isNull(psiAnnotations)) {
            return StringUtils.EMPTY;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            String qualifiedName = psiAnnotation.getQualifiedName();
            if (StringUtils.equalsAny(qualifiedName, "javax.validation.constraints.Size", "org.hibernate.validator.constraints.Length")) {
                String validatorText = StringUtils.EMPTY;
                PsiAnnotationMemberValue minMember = psiAnnotation.findAttributeValue("min");
                if (Objects.nonNull(minMember)) {
                    validatorText = "min=" + minMember.getText();
                }
                PsiAnnotationMemberValue maxMember = psiAnnotation.findAttributeValue("max");
                if (Objects.nonNull(maxMember)) {
                    validatorText += ", max=" + maxMember.getText();
                }
                return "长度限制: " + validatorText;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 导入类依赖
     *
     * @param elementFactory 元素Factory
     * @param file           当前文件对象
     * @param className      类名
     */
    private void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        final PsiImportList importList = javaFile.getImportList();
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
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (StringUtils.equals(waiteImportClass.getQualifiedName(), impQualifiedName)) {
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }

    /**
     * 类是否为controller
     *
     * @param psiClass 类元素
     * @return boolean
     */
    private boolean isController(PsiClass psiClass) {
        PsiAnnotation[] psiAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (StringUtils.equalsAny(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.CONTROLLER_ANNOTATION.getName(),
                    SpringAnnotationEnum.REST_CONTROLLER_ANNOTATION.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取RequestMapping注解属性
     *
     * @param psiAnnotations 注解元素数组
     * @param attributeName  属性名
     * @return String 属性值
     */
    private String getMappingAttribute(PsiAnnotation[] psiAnnotations, String attributeName) {
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            RequestAnnotationEnum requestAnnotationEnum = RequestAnnotationEnum.getEnumByQualifiedName(psiAnnotation.getQualifiedName());
            if (Objects.isNull(requestAnnotationEnum)) {
                continue;
            }
            switch (requestAnnotationEnum) {
                case REQUEST_MAPPING:
                    return getAttribute(psiAnnotation, attributeName, RequestAnnotationEnum.REQUEST_MAPPING.getMethodName());
                case POST_MAPPING:
                    return RequestAnnotationEnum.POST_MAPPING.getMethodName();
                case GET_MAPPING:
                    return RequestAnnotationEnum.GET_MAPPING.getMethodName();
                case DELETE_MAPPING:
                    return RequestAnnotationEnum.DELETE_MAPPING.getMethodName();
                case PATCH_MAPPING:
                    return RequestAnnotationEnum.PATCH_MAPPING.getMethodName();
                case PUT_MAPPING:
                    return RequestAnnotationEnum.PUT_MAPPING.getMethodName();
                default:
                    break;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取注解属性
     *
     * @param psiAnnotation 注解全路径
     * @param attributeName 注解属性名
     * @return 属性值
     */
    private String getAttribute(PsiAnnotation psiAnnotation, String attributeName, String comment) {
        if (Objects.isNull(psiAnnotation)) {
            return comment;
        }
        PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findAttributeValue(attributeName);
        return Objects.isNull(psiAnnotationMemberValue) ? comment : psiAnnotationMemberValue.getText();
    }

    /**
     * 写入到文件
     *
     * @param name                 注解名
     * @param qualifiedName        注解全包名
     * @param annotationText       生成注解文本
     * @param psiModifierListOwner 当前写入对象
     */
    private void doWrite(String name, String qualifiedName, String annotationText, PsiModifierListOwner psiModifierListOwner) {
        PsiAnnotation psiAnnotationDeclare = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner);
        final PsiNameValuePair[] attributes = psiAnnotationDeclare.getParameterList().getAttributes();
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        PsiAnnotation existAnnotation = modifierList.findAnnotation(qualifiedName);
        if (Objects.nonNull(existAnnotation)) {
            existAnnotation.delete();
        }
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = modifierList.addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 清洗特殊字符
     *
     * @param value
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/24 10:39
     */
    private String washSpecialChar(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        value = StringUtils.equals(value, "\"\"") ? StringUtils.EMPTY : value;
        return StringUtils.contains(value, "\"") ? StringUtils.replace(value, "\"", StringUtils.EMPTY) : value;
    }

}

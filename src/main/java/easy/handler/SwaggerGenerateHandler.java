package easy.handler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import easy.service.TranslateService;
import easy.util.SwaggerCommentUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private static final String MAPPING_VALUE = "value";
    private static final String MAPPING_METHOD = "method";
    private static final String REQUEST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String POST_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PostMapping";
    private static final String GET_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.GetMapping";
    private static final String DELETE_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.DeleteMapping";
    private static final String PATCH_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PatchMapping";
    private static final String PUT_MAPPING_ANNOTATION = "org.springframework.web.bind.annotation.PutMapping";
    private static final String REQUEST_PARAM_TEXT = "org.springframework.web.bind.annotation.RequestParam";
    private static final String REQUEST_HEADER_TEXT = "org.springframework.web.bind.annotation.RequestHeader";
    private static final String PATH_VARIABLE_TEXT = "org.springframework.web.bind.annotation.PathVariable";
    private static final String REQUEST_BODY_TEXT = "org.springframework.web.bind.annotation.RequestBody";
    private static final String CONTROLLER_ANNOTATION = "org.springframework.stereotype.Controller";
    private static final String REST_CONTROLLER_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

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
            // 获取注释
            this.generateClassAnnotation(psiClass, isController);
            if (isController) {
                // 类方法列表
                PsiMethod[] methods = psiClass.getMethods();
                if (methods.length == 0) {
                    return;
                }
                for (PsiMethod psiMethod : methods) {
                    this.generateMethodAnnotation(psiMethod);
                }
            } else {
                PsiClass[] innerClasses = psiClass.getInnerClasses();
                if (innerClasses.length == 0) {
                    return;
                }
                for (PsiClass innerClass : innerClasses) {
                    this.generateClassAnnotation(innerClass, false);
                    PsiField[] fields = innerClass.getAllFields();
                    if (fields.length == 0) {
                        return;
                    }
                    for (PsiField psiField : fields) {
                        this.generateFieldAnnotation(psiField);
                    }
                }
                // 类属性列表
                PsiField[] fields = psiClass.getAllFields();
                if (fields.length == 0) {
                    return;
                }
                for (PsiField psiField : fields) {
                    this.generateFieldAnnotation(psiField);
                }
            }
        });
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
        PsiAnnotation existAnnotation = psiModifierListOwner.getModifierList().findAnnotation(qualifiedName);
        if (Objects.nonNull(existAnnotation)) {
            log.warn("existAnnotation:" + existAnnotation.getQualifiedName());
            log.warn("existAnnotation:" + existAnnotation.getText());
            existAnnotation.delete();
        }
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = psiModifierListOwner.getModifierList().addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 类是否为controller
     *
     * @param psiClass 类元素
     * @return boolean
     */
    private void generateSelection(PsiClass psiClass, String selectionText, boolean isController) {
        if (StringUtils.equals(selectionText, psiClass.getName())) {
            this.generateClassAnnotation(psiClass, isController);
        }
        PsiMethod[] methods = psiClass.getMethods();
        if (methods.length == 0) {
            return;
        }
        for (PsiMethod psiMethod : methods) {
            if (StringUtils.equals(selectionText, psiMethod.getName())) {
                this.generateMethodAnnotation(psiMethod);
                return;
            }
        }
        PsiField[] fields = psiClass.getAllFields();
        for (PsiField psiField : fields) {
            if (StringUtils.equals(selectionText, psiField.getNameIdentifier().getText())) {
                this.generateFieldAnnotation(psiField);
                return;
            }
        }
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
            if (StringUtils.equalsAny(psiAnnotation.getQualifiedName(), CONTROLLER_ANNOTATION, REST_CONTROLLER_ANNOTATION)) {
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
            switch (Objects.requireNonNull(psiAnnotation.getQualifiedName())) {
                case REQUEST_MAPPING_ANNOTATION:
                    String attribute = getAttribute(psiAnnotation, attributeName, "");
                    if (Objects.equals("\"\"", attribute)) {
                        return StringUtils.EMPTY;
                    }
                    return attribute;
                case POST_MAPPING_ANNOTATION:
                    return "POST";
                case GET_MAPPING_ANNOTATION:
                    return "GET";
                case DELETE_MAPPING_ANNOTATION:
                    return "DELETE";
                case PATCH_MAPPING_ANNOTATION:
                    return "PATCH";
                case PUT_MAPPING_ANNOTATION:
                    return "PUT";
                default:
                    break;
            }
        }
        return "";
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
            return "\"" + comment + "\"";
        }
        PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findDeclaredAttributeValue(attributeName);
        return Objects.isNull(psiAnnotationMemberValue) ? "\"" + comment + "\"" : psiAnnotationMemberValue.getText();
    }

    /**
     * 生成类注解
     *
     * @param psiClass
     * @param isController
     * @return void
     * @author mabin
     * @date 2023/11/4 16:47
     */
    private void generateClassAnnotation(PsiClass psiClass, boolean isController) {
        PsiComment classComment = null;
        PsiElement[] psiElements = psiClass.getChildren();
        if (psiElements.length == 0) {
            return;
        }
        for (PsiElement tmpEle : psiElements) {
            if (tmpEle instanceof PsiComment) {
                classComment = (PsiComment) tmpEle;
                String tmpText = classComment.getText();
                String commentDesc = SwaggerCommentUtil.getCommentDesc(tmpText);
                String annotationFromText;
                String annotation;
                String qualifiedName;
                if (isController) {
                    annotation = "Api";
                    qualifiedName = "io.swagger.annotations.Api";
                    annotationFromText = String.format("@%s(tags = {\"%s\"})", annotation, commentDesc);
                } else {
                    annotation = "ApiModel";
                    qualifiedName = "io.swagger.annotations.ApiModel";
                    annotationFromText = String.format("@%s(description = \"%s\")", annotation, commentDesc);
                }
                this.doWrite(annotation, qualifiedName, annotationFromText, psiClass);
            }
        }
        if (Objects.isNull(classComment)) {
            String annotationFromText;
            String annotation;
            String qualifiedName;
            String commentDesc = translateService.translate(psiClass.getNameIdentifier().getText());
            if (isController) {
                annotation = "Api";
                qualifiedName = "io.swagger.annotations.Api";
                annotationFromText = String.format("@%s(value = %s)", annotation, commentDesc);
            } else {
                annotation = "ApiModel";
                qualifiedName = "io.swagger.annotations.ApiModel";
                annotationFromText = String.format("@%s(description = \"%s\")", annotation, commentDesc);
            }
            this.doWrite(annotation, qualifiedName, annotationFromText, psiClass);
        }
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
        String commentDesc = "";
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
        PsiAnnotation apiOperationExist = psiMethod.getModifierList().findAnnotation("io.swagger.annotations.ApiOperation");
        String apiOperationAttrValue = this.getAttribute(apiOperationExist, "value", commentDesc);
        String apiOperationAttrNotes = this.getAttribute(apiOperationExist, "notes", commentDesc);

        // 如果注解和注释都不存在, 尝试自动翻译方法名作为value值
        if (StringUtils.isBlank(apiOperationAttrValue)) {
            apiOperationAttrValue = translateService.translate(psiMethod.getNameIdentifier().getText());
        }

        PsiAnnotation[] psiAnnotations = psiMethod.getModifierList().getAnnotations();
        String methodValue = this.getMappingAttribute(psiAnnotations, MAPPING_METHOD);
        StringBuilder apiOperationAnnotationText = new StringBuilder();
        if (StringUtils.isNotEmpty(methodValue)) {
            methodValue = methodValue.substring(methodValue.indexOf(".") + 1);
            apiOperationAnnotationText.append("@ApiOperation(value = ").append(apiOperationAttrValue);
            if (StringUtils.isNotBlank(apiOperationAttrNotes)) {
                apiOperationAnnotationText.append(", notes = ").append(apiOperationAttrNotes);
            }
            apiOperationAnnotationText.append(", ").append("httpMethod = \"").append(methodValue).append("\")");
        } else {
            apiOperationAnnotationText.append("@ApiOperation(value = ").append(apiOperationAttrValue);
            if (StringUtils.isNotBlank(apiOperationAttrNotes)) {
                apiOperationAnnotationText.append(", notes = ").append(apiOperationAttrNotes);
            }
            apiOperationAnnotationText.append(")");
        }

        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        List<String> apiImplicitParamList = new ArrayList<>(psiParameters.length);
        for (PsiParameter psiParameter : psiParameters) {
            PsiType psiType = psiParameter.getType();
            String paramType = "query";
            String required = null;
            for (PsiAnnotation psiAnnotation : psiParameter.getModifierList().getAnnotations()) {
                if (StringUtils.isBlank(psiAnnotation.getQualifiedName())) {
                    break;
                }
                switch (psiAnnotation.getQualifiedName()) {
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
                required = this.getAttribute(psiAnnotation, "required", "");
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
            apiImplicitParamText.append("@ApiImplicitParam(paramType = ").append(paramType)
                    .append(", name = ").append(paramName);
            if (StringUtils.isBlank(paramDesc)) {
                paramDesc = translateService.translate(paramName);
            }
            if (StringUtils.isNotBlank(paramDesc)) {
                apiImplicitParamText.append(", value = ").append(paramDesc);
            }
            apiImplicitParamText.append(", dataType = ").append(dataType);
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
            apiImplicitParamsAnnotationText = apiImplicitParamList.stream().collect(Collectors.joining(",\n", "@ApiImplicitParams({\n", "\n})"));
            complex = true;
        }

        this.doWrite("ApiOperation", "io.swagger.annotations.ApiOperation", apiOperationAnnotationText.toString(), psiMethod);
        if (StringUtils.isNotEmpty(apiImplicitParamsAnnotationText)) {
            if (complex) {
                this.doWrite("ApiImplicitParams", "io.swagger.annotations.ApiImplicitParams", apiImplicitParamsAnnotationText, psiMethod);
            } else {
                this.doWrite("ApiImplicitParam", "io.swagger.annotations.ApiImplicitParam", apiImplicitParamsAnnotationText, psiMethod);
            }
        }
        addImport(elementFactory, psiFile, "ApiImplicitParam");
    }

    /**
     * 生成属性注解
     *
     * @param psiField
     * @return void
     * @author mabin
     * @date 2023/11/4 15:57
     */
    private void generateFieldAnnotation(PsiField psiField) {
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiField.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                classComment = (PsiComment) tmpEle;
                String tmpText = classComment.getText();
                String commentDesc = SwaggerCommentUtil.getCommentDesc(tmpText);
                String apiModelPropertyText;
                if (isValidate(psiField.getAnnotations())) {
                    apiModelPropertyText = String.format("@ApiModelProperty(value=\"%s\", required=true)", commentDesc);
                } else {
                    apiModelPropertyText = String.format("@ApiModelProperty(value=\"%s\")", commentDesc);
                }
                this.doWrite("ApiModelProperty", "io.swagger.annotations.ApiModelProperty", apiModelPropertyText, psiField);
            }
        }
        if (Objects.isNull(classComment)) {
            String fieldName = psiField.getNameIdentifier().getText();
            String apiModelPropertyText;
            if (StringUtils.equals(fieldName, "serialVersionUID")) {
                apiModelPropertyText = "@ApiModelProperty(hidden = true)";
            } else {
                String commentDesc = translateService.translate(fieldName);
                if (StringUtils.isNotBlank(commentDesc)) {
                    apiModelPropertyText = isValidate(psiField.getAnnotations()) ? String.format("@ApiModelProperty(value=\"%s\", required=true)", commentDesc)
                            : String.format("@ApiModelProperty(value=\"%s\")", commentDesc);
                } else {
                    apiModelPropertyText = "@ApiModelProperty(hidden = true)";
                }
            }
            this.doWrite("ApiModelProperty", "io.swagger.annotations.ApiModelProperty", apiModelPropertyText, psiField);
        }
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
        if (Objects.isNull(psiAnnotation) || psiAnnotation.length == 0) {
            return false;
        }
        String a = "javax.validation.constraints";
        for (PsiAnnotation annotation : psiAnnotation) {
            if (StringUtils.startsWith(annotation.getQualifiedName(), a)) {
                return true;
            }
        }
        return false;
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
        if (importList == null) {
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
}

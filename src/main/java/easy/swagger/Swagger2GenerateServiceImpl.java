package easy.swagger;

import com.intellij.psi.*;
import easy.base.Constants;
import easy.enums.BaseTypeEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.PsiElementUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Swagger2GenerateServiceImpl extends AbstractSwaggerGenerateService {


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
        String attrValue;
        if (isController) {
            attrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.API.getClassPackage()), commentDesc, List.of(Constants.ANNOTATION_ATTR.TAGS));
            if (StringUtils.isNotBlank(attrValue) && StringUtils.containsAny(attrValue, "{", "}")) {
                attrValue = StringUtils.substringBetween(attrValue, "{", "}");
            }
        } else {
            attrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.API_MODEL.getClassPackage()), commentDesc, List.of(Constants.ANNOTATION_ATTR.VALUE, Constants.ANNOTATION_ATTR.DESCRIPTION));
        }
        if (StringUtils.isBlank(attrValue)) {
            String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
            attrValue = translateService.translate(className);
            if (StringUtils.isBlank(attrValue)) {
                attrValue = isController ? className + SwaggerAnnotationEnum.API.getClassName() : className + SwaggerAnnotationEnum.API_MODEL.getClassName();
            }
        }
        String annotationFromText = isController ? String.format("@%s(%s = {\"%s\"})", SwaggerAnnotationEnum.API.getClassName(), Constants.ANNOTATION_ATTR.TAGS, attrValue) : String.format("@%s(\"%s\")", SwaggerAnnotationEnum.API_MODEL.getClassName(), attrValue);
        doWrite(isController ? SwaggerAnnotationEnum.API.getClassName() : SwaggerAnnotationEnum.API_MODEL.getClassName(), isController ? SwaggerAnnotationEnum.API.getClassPackage() : SwaggerAnnotationEnum.API_MODEL.getClassPackage(), annotationFromText, psiClass);
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
        String apiOperationAnnotationText = buildApiOperationAnnotation(psiMethod);
        List<String> apiImplicitParamList = buildApiImplicitParamsAnnotation(psiMethod);

        doWrite(SwaggerAnnotationEnum.API_OPERATION.getClassName(), SwaggerAnnotationEnum.API_OPERATION.getClassPackage(), apiOperationAnnotationText, psiMethod);
        if (CollectionUtils.isNotEmpty(apiImplicitParamList)) {
            if (apiImplicitParamList.size() == Constants.NUM.ONE) {
                doWrite(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassName(), SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassPackage(),
                        apiImplicitParamList.get(0), psiMethod);
            } else {
                doWrite(SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassName(), SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassPackage(),
                        apiImplicitParamList.stream().collect(Collectors.joining(",\n", Constants.AT +
                                SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassName() + "({\n", "\n})")), psiMethod);
                addImport(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassName());
            }
        }
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
        String fieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
        StringBuilder apiModelPropertyText = new StringBuilder().append(Constants.AT).append(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassName());
        if (StringUtils.equals(fieldName, Constants.UID)) {
            apiModelPropertyText.append(String.format("(%s = true)", Constants.ANNOTATION_ATTR.HIDDEN));
        } else {
            String fieldCommentDesc = StringUtils.EMPTY;
            if (Objects.nonNull(psiField.getDocComment())) {
                fieldCommentDesc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(psiField.getDocComment().getChildren()));
            }
            PsiAnnotation apiModelPropertyAnnotation = psiField.getAnnotation(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage());
            String apiModelPropertyAttrValue = PsiElementUtil.getAnnotationAttributeValue(apiModelPropertyAnnotation, fieldCommentDesc, List.of(Constants.ANNOTATION_ATTR.VALUE));
            String apiModelPropertyAttrNotes = PsiElementUtil.getAnnotationAttributeValue(apiModelPropertyAnnotation, List.of(Constants.ANNOTATION_ATTR.NOTES));
            if (StringUtils.isBlank(apiModelPropertyAttrValue)) {
                apiModelPropertyAttrValue = translateService.translate(fieldName);
                if (StringUtils.isBlank(apiModelPropertyAttrValue)) {
                    apiModelPropertyAttrValue = fieldName;
                }
            }
            String validatorText = getValidatorLimitText(psiField);
            if (StringUtils.isBlank(apiModelPropertyAttrNotes)) {
                if (StringUtils.isNotBlank(validatorText)) {
                    apiModelPropertyAttrNotes = validatorText;
                }
            } else if (StringUtils.isNotBlank(validatorText) && !StringUtils.contains(apiModelPropertyAttrNotes, validatorText)) {
                apiModelPropertyAttrNotes += " (" + validatorText + ")";
            }
            apiModelPropertyText.append("(").append(Constants.ANNOTATION_ATTR.VALUE).append(" = \"").append(apiModelPropertyAttrValue).append("\"");
            if (StringUtils.isNotBlank(apiModelPropertyAttrNotes)) {
                apiModelPropertyText.append(", ").append(Constants.ANNOTATION_ATTR.NOTES).append(" = \"").append(apiModelPropertyAttrNotes).append("\"");
            }
            apiModelPropertyText.append(isValidate(psiField) ? String.format(", %s=true)", Constants.ANNOTATION_ATTR.REQUIRED) : ")");
        }
        doWrite(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassName(), SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage(), apiModelPropertyText.toString(), psiField);
    }

    /**
     * 构建@ApiImplicitParams注解文本列表
     *
     * @param psiMethod psi法
     * @return {@link java.util.List<java.lang.String>}
     * @author mabin
     * @date 2024/04/24 14:18
     */
    private List<String> buildApiImplicitParamsAnnotation(PsiMethod psiMethod) {
        Map<String, String> paramsMap = PsiElementUtil.parseJavaDocMethodParams(psiMethod);
        List<String> apiImplicitParamList = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            if (Arrays.stream(psiParameter.getAnnotations()).anyMatch(psiAnnotation -> StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.API_IGNORE.getClassPackage()))) {
                continue;
            }
            String dataType = getDataType(psiParameter);
            String paramType = getParamType(psiParameter);
            String required = getParamRequired(psiParameter);
            String paramName = PsiElementUtil.getPsiElementNameIdentifierText(psiParameter);
            String paramDesc = StringUtils.isBlank(paramsMap.get(paramName)) ? translateService.translate(paramName) : paramsMap.get(paramName);

            StringBuilder apiImplicitParamText = new StringBuilder();
            apiImplicitParamText.append(Constants.AT).append(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassName())
                    .append("(").append(Constants.ANNOTATION_ATTR.PARAM_TYPE).append(" = \"").append(paramType).append("\"")
                    .append(", ").append(Constants.ANNOTATION_ATTR.NAME).append(" = \"").append(paramName).append("\"")
                    .append(", ").append(Constants.ANNOTATION_ATTR.VALUE).append(" = \"").append(paramDesc).append("\"");

            if (Boolean.TRUE.equals(BaseTypeEnum.isBaseType(dataType)) || StringUtils.equalsAny(dataType, "file")) {
                apiImplicitParamText.append(", ").append(Constants.ANNOTATION_ATTR.DATA_TYPE).append(" = \"").append(dataType).append("\"");
            } else {
                apiImplicitParamText.append(", ").append(Constants.ANNOTATION_ATTR.DATA_TYPE_CLASS).append(" = ");
                if (StringUtils.containsAny(dataType, "<", ">")) {
                    if (!StringUtils.containsAnyIgnoreCase(dataType, "map")) {
                        String collDataType = StringUtils.substringBetween(dataType, "<", ">");
                        apiImplicitParamText.append(collDataType).append(".class");
                    } else {
                        apiImplicitParamText.append("Map.class");
                    }
                    apiImplicitParamText.append(", ").append(Constants.ANNOTATION_ATTR.ALLOW_MULTIPLE).append(" = true");
                } else if (StringUtils.containsAny(dataType, "[", "]")) {
                    String collDataType = StringUtils.substringBefore(dataType, "[");
                    apiImplicitParamText.append(collDataType).append(".class").append(", ").append(Constants.ANNOTATION_ATTR.ALLOW_MULTIPLE).append(" = true");
                } else {
                    apiImplicitParamText.append(dataType).append(".class");
                }
            }
            if (StringUtils.equals(required, "true")) {
                apiImplicitParamText.append(", ").append(Constants.ANNOTATION_ATTR.REQUIRED).append(" = true");
            }
            apiImplicitParamText.append(")");
            apiImplicitParamList.add(apiImplicitParamText.toString());
        }
        return apiImplicitParamList;
    }

    /**
     * 构建@ApiOperation注解
     *
     * @param psiMethod psi法
     * @return {@link String}
     * @author mabin
     * @date 2024/04/24 14:14
     */
    private String buildApiOperationAnnotation(PsiMethod psiMethod) {
        String methodCommentDesc = StringUtils.EMPTY;
        if (Objects.nonNull(psiMethod.getDocComment())) {
            methodCommentDesc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(psiMethod.getDocComment().getChildren()));
        }
        PsiAnnotation apiOperation = psiMethod.getAnnotation(SwaggerAnnotationEnum.API_OPERATION.getClassPackage());
        String apiOperationValue = PsiElementUtil.getAnnotationAttributeValue(apiOperation, methodCommentDesc, List.of(Constants.ANNOTATION_ATTR.VALUE));
        if (StringUtils.isBlank(apiOperationValue)) {
            apiOperationValue = translateService.translate(PsiElementUtil.getPsiElementNameIdentifierText(psiMethod));
        }
        StringBuilder apiOperationAnnotationText = new StringBuilder();
        apiOperationAnnotationText.append(Constants.AT).append(SwaggerAnnotationEnum.API_OPERATION.getClassName())
                .append("(").append(Constants.ANNOTATION_ATTR.VALUE).append(" = \"").append(apiOperationValue).append("\"");
        String apiOperationNotes = PsiElementUtil.getAnnotationAttributeValue(apiOperation, List.of(Constants.ANNOTATION_ATTR.NOTES));
        if (StringUtils.isNotBlank(apiOperationNotes)) {
            apiOperationAnnotationText.append(", ").append(Constants.ANNOTATION_ATTR.NOTES).append(" = \"").append(apiOperationNotes).append("\"");
        }
        String apiOperationMethod = getHttpMethodName(psiMethod.getAnnotations());
        if (StringUtils.isNotBlank(apiOperationMethod)) {
            apiOperationAnnotationText.append(", ").append(Constants.ANNOTATION_ATTR.HTTP_METHOD).append(" = \"").append(apiOperationMethod).append("\"");
        }
        String classTagAttrValue = getClassTagAttrValue();
        if (StringUtils.isNotBlank(classTagAttrValue)) {
            apiOperationAnnotationText.append(", ").append(Constants.ANNOTATION_ATTR.TAGS).append(" = {\"").append(classTagAttrValue).append("\"}");
        }
        apiOperationAnnotationText.append(")");
        return apiOperationAnnotationText.toString();
    }

}

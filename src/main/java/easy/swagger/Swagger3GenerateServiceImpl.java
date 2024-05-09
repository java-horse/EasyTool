package easy.swagger;

import com.intellij.psi.*;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SpringAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.PsiElementUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Swagger3GenerateServiceImpl extends AbstractSwaggerGenerateService {

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
        String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
        if (isController) {
            PsiAnnotation tagAnnotation = psiClass.getAnnotation(SwaggerAnnotationEnum.TAG.getClassPackage());
            String nameAttrValue = PsiElementUtil.getAnnotationAttributeValue(tagAnnotation, commentDesc, List.of(Constants.ANNOTATION_ATTR.NAME));
            if (StringUtils.isBlank(nameAttrValue)) {
                nameAttrValue = translateService.translate(className);
                if (StringUtils.isBlank(nameAttrValue)) {
                    nameAttrValue = className + SwaggerAnnotationEnum.TAG.getClassName();
                }
            }
            String descAttrValue = PsiElementUtil.getAnnotationAttributeValue(tagAnnotation, List.of(Constants.ANNOTATION_ATTR.DESCRIPTION));
            String tagAnnotationText = String.format("%s%s(%s = \"%s\", %s = \"%s\")", Constants.AT, SwaggerAnnotationEnum.TAG.getClassName(), Constants.ANNOTATION_ATTR.NAME,
                    nameAttrValue, Constants.ANNOTATION_ATTR.DESCRIPTION, StringUtils.isBlank(descAttrValue) ? className : descAttrValue);
            doWrite(SwaggerAnnotationEnum.TAG.getClassName(), SwaggerAnnotationEnum.TAG.getClassPackage(), tagAnnotationText, psiClass);
        } else {
            String titleAttrValue = PsiElementUtil.getAnnotationAttributeValue(psiClass.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage()), commentDesc, List.of(Constants.ANNOTATION_ATTR.TITLE));
            if (StringUtils.isBlank(titleAttrValue)) {
                titleAttrValue = translateService.translate(className);
                if (StringUtils.isBlank(titleAttrValue)) {
                    titleAttrValue = className + SwaggerAnnotationEnum.SCHEMA.getClassName();
                }
            }
            String schemaAnnotationText = String.format("%s%s(%s = \"%s\")", Constants.AT, SwaggerAnnotationEnum.SCHEMA.getClassName(), Constants.ANNOTATION_ATTR.TITLE, titleAttrValue);
            doWrite(SwaggerAnnotationEnum.SCHEMA.getClassName(), SwaggerAnnotationEnum.SCHEMA.getClassPackage(), schemaAnnotationText, psiClass);
        }
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
        String operationText = buildOperationAnnotation(psiMethod);
        List<String> parameterList = buildParameterAnnotation(psiMethod);

        doWrite(SwaggerAnnotationEnum.OPERATION.getClassName(), SwaggerAnnotationEnum.OPERATION.getClassPackage(), operationText, psiMethod);
        if (CollectionUtils.isNotEmpty(parameterList)) {
            if (parameterList.size() == Constants.NUM.ONE) {
                doWrite(SwaggerAnnotationEnum.PARAMETER.getClassName(), SwaggerAnnotationEnum.PARAMETER.getClassPackage(), parameterList.get(0), psiMethod);
            } else {
                doWrite(SwaggerAnnotationEnum.PARAMETERS.getClassName(), SwaggerAnnotationEnum.PARAMETERS.getClassPackage(),
                        parameterList.stream().collect(Collectors.joining(",\n", Constants.AT +
                                SwaggerAnnotationEnum.PARAMETERS.getClassName() + "({\n", "\n})")), psiMethod);
                addImport(SwaggerAnnotationEnum.PARAMETER.getClassName());
            }
            addImport(ExtraPackageNameEnum.PARAMETER_IN.getClassName());
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
        StringBuilder schemaBuilder = new StringBuilder().append(Constants.AT).append(SwaggerAnnotationEnum.SCHEMA.getClassName());
        if (StringUtils.equals(fieldName, Constants.UID)) {
            schemaBuilder.append(String.format("(%s = true)", Constants.ANNOTATION_ATTR.HIDDEN));
        } else {
            String fieldCommentDesc = StringUtils.EMPTY;
            if (Objects.nonNull(psiField.getDocComment())) {
                fieldCommentDesc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(psiField.getDocComment().getChildren()));
            }
            PsiAnnotation schemaAnnotation = psiField.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage());
            String titleAttrValue = PsiElementUtil.getAnnotationAttributeValue(schemaAnnotation, fieldCommentDesc, List.of(Constants.ANNOTATION_ATTR.TITLE));
            String descAttrValue = PsiElementUtil.getAnnotationAttributeValue(schemaAnnotation, List.of(Constants.ANNOTATION_ATTR.DESCRIPTION));
            if (StringUtils.isBlank(titleAttrValue)) {
                titleAttrValue = translateService.translate(fieldName);
                if (StringUtils.isBlank(titleAttrValue)) {
                    titleAttrValue = fieldName;
                }
            }
            schemaBuilder.append("(").append(", ").append(Constants.ANNOTATION_ATTR.TITLE).append(" = \"").append(titleAttrValue).append("\"");
            if (StringUtils.isNotBlank(descAttrValue)) {
                schemaBuilder.append(", ").append(Constants.ANNOTATION_ATTR.DESCRIPTION).append(" = \"").append(descAttrValue).append("\"");
            }
            String validatorLimitText = getValidatorLimitText(psiField);
            if (StringUtils.isNotBlank(validatorLimitText)) {
                schemaBuilder.append(validatorLimitText);
            }
            if (Objects.nonNull(psiField.getAnnotation(ExtraPackageNameEnum.NULL.getName()))) {
                schemaBuilder.append(", ").append(Constants.ANNOTATION_ATTR.NULLABLE).append(" = true");
            }
            if (isDeprecated(psiField)) {
                schemaBuilder.append(", ").append(Constants.ANNOTATION_ATTR.DEPRECATED).append(" = true");
            }
            String patternValue = PsiElementUtil.getAnnotationAttributeValue(psiField.getAnnotation(ExtraPackageNameEnum.DATE_TIME_FORMAT.getName()), List.of(Constants.ANNOTATION_ATTR.PATTERN));
            if (StringUtils.isNotBlank(patternValue)) {
                schemaBuilder.append(", ").append(Constants.ANNOTATION_ATTR.PATTERN).append(" = \"").append(patternValue).append("\"");
            }
            schemaBuilder.append(isValidate(psiField) ? String.format(", %s = Schema.RequiredMode.REQUIRED)", Constants.ANNOTATION_ATTR.REQUIRED_MODE) : ")");
        }
        doWrite(SwaggerAnnotationEnum.SCHEMA.getClassName(), SwaggerAnnotationEnum.SCHEMA.getClassPackage(), schemaBuilder.toString(), psiField);
    }

    /**
     * 构建@Operation注解
     *
     * @param psiMethod psi法
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/27 14:47
     */
    private String buildOperationAnnotation(PsiMethod psiMethod) {
        String methodCommentDesc = StringUtils.EMPTY;
        if (Objects.nonNull(psiMethod.getDocComment())) {
            methodCommentDesc = PsiElementUtil.parseJavaDocDesc(Arrays.asList(psiMethod.getDocComment().getChildren()));
        }
        PsiAnnotation operation = psiMethod.getAnnotation(SwaggerAnnotationEnum.OPERATION.getClassPackage());
        String summaryAttrValue = PsiElementUtil.getAnnotationAttributeValue(operation, methodCommentDesc, List.of(Constants.ANNOTATION_ATTR.SUMMARY));
        if (StringUtils.isBlank(summaryAttrValue)) {
            summaryAttrValue = translateService.translate(PsiElementUtil.getPsiElementNameIdentifierText(psiMethod));
        }
        StringBuilder operationBuilder = new StringBuilder()
                .append(Constants.AT).append(SwaggerAnnotationEnum.OPERATION.getClassName())
                .append("(").append(Constants.ANNOTATION_ATTR.SUMMARY).append(" = \"")
                .append(summaryAttrValue).append("\"");
        String descAttrValue = PsiElementUtil.getAnnotationAttributeValue(operation, List.of(Constants.ANNOTATION_ATTR.DESCRIPTION));
        if (StringUtils.isNotBlank(descAttrValue)) {
            operationBuilder.append(", ").append(Constants.ANNOTATION_ATTR.DESCRIPTION).append(" = \"")
                    .append(descAttrValue).append("\"");
        }
        String apiOperationMethod = getHttpMethodName(psiMethod.getAnnotations());
        if (StringUtils.isNotBlank(apiOperationMethod)) {
            operationBuilder.append(", ").append(Constants.ANNOTATION_ATTR.METHOD).append(" = \"")
                    .append(apiOperationMethod).append("\"");
        }
        String classTagAttrValue = getClassTagAttrValue();
        if (StringUtils.isNotBlank(classTagAttrValue)) {
            operationBuilder.append(", ").append(Constants.ANNOTATION_ATTR.TAGS).append(" = {\"")
                    .append(classTagAttrValue).append("\"}");
        }
        if (isDeprecated(psiMethod)) {
            operationBuilder.append(", ").append(Constants.ANNOTATION_ATTR.DEPRECATED).append(" = true");
        }
        operationBuilder.append(")");
        return operationBuilder.toString();
    }

    /**
     * 构建Parameter注解
     *
     * @param psiMethod psi法
     * @return {@link java.util.List<java.lang.String>}
     * @author mabin
     * @date 2024/04/27 14:57
     */
    private List<String> buildParameterAnnotation(PsiMethod psiMethod) {
        Map<String, String> paramsMap = PsiElementUtil.parseJavaDocMethodParams(psiMethod);
        List<String> parameterList = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            if (Arrays.stream(psiParameter.getAnnotations())
                    .anyMatch(psiAnnotation -> (StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.PARAMETER.getClassPackage())
                            && StringUtils.equalsIgnoreCase(PsiElementUtil.getAnnotationAttributeValue(psiAnnotation, List.of(Constants.ANNOTATION_ATTR.HIDDEN)), "true"))
                            || StringUtils.equals(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.REQUEST_BODY_TEXT.getName()))) {
                continue;
            }
            String paramName = PsiElementUtil.getPsiElementNameIdentifierText(psiParameter);
            String paramDesc = StringUtils.isBlank(paramsMap.get(paramName)) ? translateService.translate(paramName) : paramsMap.get(paramName);
            String required = getParamRequired(psiParameter);
            String paramIn = getParamType(psiParameter);
            StringBuilder parameterBuilder = new StringBuilder().append(Constants.AT).append(SwaggerAnnotationEnum.PARAMETER.getClassName())
                    .append("(").append(Constants.ANNOTATION_ATTR.NAME).append(" = \"").append(paramName).append("\"");
            if (StringUtils.isNotBlank(paramDesc)) {
                parameterBuilder.append(", ").append(Constants.ANNOTATION_ATTR.DESCRIPTION).append(" = \"").append(paramDesc).append("\"");
            }
            if (StringUtils.isNotBlank(paramIn)) {
                parameterBuilder.append(", ").append(Constants.ANNOTATION_ATTR.IN).append(" = ").append(paramIn);
            }
            if (StringUtils.equals(required, "true")) {
                parameterBuilder.append(", ").append(Constants.ANNOTATION_ATTR.REQUIRED).append(" = true");
            }
            parameterBuilder.append(")");
            parameterList.add(parameterBuilder.toString());
        }
        return parameterList;
    }

}

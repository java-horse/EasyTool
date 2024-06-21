package easy.api.parse.parser;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.javadoc.PsiDocMethodOrFieldRef;
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import easy.api.model.common.Value;
import easy.api.parse.model.Jsr303Info;
import easy.api.parse.model.TypeParseContext;
import easy.api.parse.util.*;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.util.PsiElementUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析辅助工具类
 */
public class ParseHelper {

    private final Project project;
    private final Module module;

    public ParseHelper(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    //----------------------- 接口Api相关 ------------------------------------//

    /**
     * 获取接口分类
     */
    public String getDeclareApiCategory(PsiClass psiClass) {
        // 优先级: 文档注释标记@module > @menu > Swagger的@Api/@Tag > 文档注释第一行
        String category = PsiDocCommentUtils.getDocCommentTagText(psiClass, Constants.DOC_TAG.MODULE);
        if (StringUtils.isBlank(category)) {
            category = PsiDocCommentUtils.getDocCommentTagText(psiClass, Constants.DOC_TAG.CATEGORY);
        }
        if (StringUtils.isBlank(category)) {
            category = PsiSwaggerUtils.getApiCategory(psiClass);
        }
        if (StringUtils.isBlank(category)) {
            category = PsiDocCommentUtils.getDocCommentTitle(psiClass);
        }
        return category;
    }

    public String getDefaultApiCategory(PsiClass psiClass) {
        return InternalUtils.camelToLine(psiClass.getName(), null);
    }

    /**
     * 获取接口概述
     */
    public String getApiSummary(PsiMethod psiMethod) {
        // 优先级: swagger注解@ApiOperation/@Operation > 文档注释标记@description >  文档注释第一行
        String summary = PsiSwaggerUtils.getApiSummary(psiMethod);
        PsiDocComment comment = psiMethod.getDocComment();
        if (comment != null) {
            if (StringUtils.isEmpty(summary)) {
                String[] tags = {Constants.DOC_TAG.DESCRIPTION, Constants.DOC_TAG.DESCRIPTION_UP};
                for (String tag : tags) {
                    summary = PsiDocCommentUtils.getDocCommentTagText(psiMethod, tag);
                    if (StringUtils.isNotEmpty(summary)) {
                        break;
                    }
                }
            }
            if (StringUtils.isEmpty(summary)) {
                summary = Arrays.stream(comment.getDescriptionElements())
                        .filter(o -> o instanceof PsiDocToken)
                        .map(PsiElement::getText)
                        .findFirst()
                        .map(String::trim)
                        .orElse(null);
            }
        }
        return StringUtils.trim(summary);
    }

    /**
     * 获取接口描述
     */
    public String getApiDescription(PsiMethod psiMethod) {
        return PsiDocCommentUtils.getDocCommentDescription(psiMethod);
    }

    /**
     * 获取接口是否标记过期
     */
    public boolean getApiDeprecated(PsiMethod method) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(method, Deprecated.class.getName());
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(method, Constants.DOC_TAG.DEPRECATED);
        return Objects.nonNull(deprecatedTag);
    }

    /**
     * 获取接口标签: JavaDoc > Swagger的@Apioperation/@Operation
     *
     * @param method 方法
     * @return {@link List<String>}
     * @author mabin
     * @date 2024/05/13 14:40
     */
    public List<String> getApiTags(PsiMethod method) {
        String tagsContent = PsiDocCommentUtils.getDocCommentTagText(method, Constants.DOC_TAG.TAGS);
        if (StringUtils.isBlank(tagsContent)) {
            PsiAnnotation operationAnnotation = method.getAnnotation(SwaggerAnnotationEnum.OPERATION.getClassPackage());
            if (Objects.nonNull(operationAnnotation)) {
                tagsContent = PsiElementUtil.getAnnotationAttributeValue(operationAnnotation, List.of(Constants.ANNOTATION_ATTR.TAGS));
            }
            if (StringUtils.isBlank(tagsContent)) {
                PsiAnnotation apiOperationAnnotation = method.getAnnotation(SwaggerAnnotationEnum.API_OPERATION.getClassPackage());
                if (Objects.nonNull(apiOperationAnnotation)) {
                    tagsContent = PsiElementUtil.getAnnotationAttributeValue(apiOperationAnnotation, List.of(Constants.ANNOTATION_ATTR.TAGS));
                }
            }
        }
        if (StringUtils.isNotBlank(tagsContent)) {
            return Splitter.on(StrUtil.COMMA).trimResults().omitEmptyStrings().splitToList(tagsContent)
                    .stream().distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    //------------------------ 参数Parameter ----------------------------//

    /**
     * 获取参数描述
     */
    public String getParameterDescription(PsiMethod method, PsiParameter parameter, Map<String, String> paramTagMap,
                                          List<Value> values) {
        // @ApiParam > @param
        String summary = PsiSwaggerUtils.getParameterDescription(method, parameter);
        if (StringUtils.isBlank(summary)) {
            summary = paramTagMap.get(parameter.getName());
        }
        if (CollectionUtils.isNotEmpty(values)) {
            String valuesText = values.stream().map(Value::getText).collect(Collectors.joining(", "));
            if (StringUtils.isEmpty(summary)) {
                summary = valuesText;
            } else {
                summary += " (" + valuesText + ")";
            }
        }
        return StringUtils.trim(summary);
    }

    /**
     * 获取参数是否必填
     */
    public Boolean getParameterRequired(PsiParameter parameter) {
        String[] annotations = {ExtraPackageNameEnum.NOT_NULL.getName(), ExtraPackageNameEnum.NOT_BLANK.getName(), ExtraPackageNameEnum.NOT_EMPTY.getName(),
                ExtraPackageNameEnum.NOT_NULL2.getName(), ExtraPackageNameEnum.NOT_BLANK2.getName(), ExtraPackageNameEnum.NOT_EMPTY2.getName()};
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(parameter, annotations);
        return Objects.nonNull(annotation);
    }

    /**
     * 获取参数可能的值
     */
    public List<Value> getParameterValues(PsiParameter parameter) {
        return getTypeValues(parameter.getType());
    }

    /**
     * 获取枚举值列表
     */
    public List<Value> getEnumValues(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields())
                .filter(field -> field instanceof PsiEnumConstant)
                .map(field -> {
                    String name = field.getName();
                    String description = PsiDocCommentUtils.getDocCommentTitle(field);
                    return new Value(name, description);
                })
                .collect(Collectors.toList());
    }

    //---------------------- 字段相关 ------------------------------//

    /**
     * 获取字段名: 优先解析JSON注解, Jackson -> FastJson2 -> Gson -> fieldName
     */
    public String getFieldName(PsiField field) {
        String property = PsiAnnotationUtils.getStringAttributeValue(field, ExtraPackageNameEnum.JSON_PROPERTY.getName());
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        property = PsiAnnotationUtils.getStringAttributeValue(field, ExtraPackageNameEnum.JSON_FIELD.getName());
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        property = PsiAnnotationUtils.getStringAttributeValue(field, ExtraPackageNameEnum.SERIALIZED_NAME.getName());
        if (StringUtils.isNotBlank(property)) {
            return property;
        }
        return field.getName();
    }

    /**
     * 获取字段描述
     */
    public String getFieldDescription(PsiField field, List<Value> values) {
        // 优先级: @ApiModelProperty > 文档注释标记@description >  文档注释第一行
        String summary = PsiSwaggerUtils.getFieldDescription(field);
        PsiDocComment comment = field.getDocComment();
        if (comment != null) {
            if (StringUtils.isEmpty(summary)) {
                summary = Arrays.stream(comment.getDescriptionElements())
                        .filter(o -> o instanceof PsiDocToken)
                        .map(PsiElement::getText)
                        .findFirst()
                        .map(String::trim)
                        .orElse(null);
            }
        }

        // 枚举
        if (values != null && !values.isEmpty()) {
            String valuesText = values.stream().map(Value::getText).collect(Collectors.joining(", "));
            if (StringUtils.isEmpty(summary)) {
                summary = valuesText;
            } else {
                summary += " (" + valuesText + ")";
            }
        }

        return StringUtils.trim(summary);
    }

    /**
     * 字段是否必填
     */
    public boolean getFieldRequired(TypeParseContext context, PsiField field) {
        List<String> validateGroups = context.getJsr303ValidateGroups();
        String[] annotations = {ExtraPackageNameEnum.NOT_NULL.getName(), ExtraPackageNameEnum.NOT_BLANK.getName(), ExtraPackageNameEnum.NOT_EMPTY.getName(),
                ExtraPackageNameEnum.NOT_NULL2.getName(), ExtraPackageNameEnum.NOT_BLANK2.getName(), ExtraPackageNameEnum.NOT_EMPTY2.getName()};
        for (String annotation : annotations) {
            PsiAnnotation target = PsiAnnotationUtils.getAnnotation(field, annotation);
            if (target == null) {
                continue;
            }

            List<String> groups = PsiAnnotationUtils.getStringArrayAttribute(target, "groups");
            boolean validateJsr303 = CollectionUtils.isEmpty(validateGroups) || !CollectionUtils.intersection(groups, validateGroups).isEmpty();
            if (validateJsr303) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字段可能的值
     */
    public List<Value> getFieldValues(PsiField field) {
        // 枚举类
        boolean isEnum = PsiTypeUtils.isEnum(field.getType());
        if (isEnum) {
            PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(this.project, this.module, field.getType());
            if (enumPsiClass != null) {
                return getEnumValues(enumPsiClass);
            }
        }

        List<Value> values = Lists.newArrayList();
        // 解析: @link文档标记
        if (field.getDocComment() != null) {
            PsiDocTag[] linkTags = Arrays.stream(field.getDocComment().getDescriptionElements())
                    .filter(e -> e instanceof PsiInlineDocTag)
                    .filter(e -> ((PsiInlineDocTag) e).getName().equals(Constants.DOC_TAG.LINK))
                    .toArray(PsiDocTag[]::new);
            for (PsiDocTag tag : linkTags) {
                List<Value> tagValues = doGetFieldValueByTag(tag);
                if (Objects.nonNull(tagValues) && !tagValues.isEmpty()) {
                    values.addAll(tagValues);
                }
            }
        }

        // 解析: @see 文档标记
        PsiDocTag[] tags = PsiDocCommentUtils.findTagsByName(field, Constants.DOC_TAG.SEE);
        for (PsiDocTag tag : tags) {
            List<Value> tagValues = doGetFieldValueByTag(tag);
            if (Objects.nonNull(tagValues) && !tagValues.isEmpty()) {
                values.addAll(tagValues);
            }
        }
        return values.stream().filter(distinctByKey(Value::getValue)).collect(Collectors.toList());
    }

    private List<Value> doGetFieldValueByTag(PsiDocTag tag) {
        PsiElement[] elements = tag.getDataElements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }
        PsiElement targetElement = elements[0];
        if (tag instanceof PsiInlineDocTag) {
            PsiElement psiElement = Arrays.stream(elements)
                    .filter(e -> e instanceof LazyParseablePsiElement || e instanceof PsiDocMethodOrFieldRef)
                    .findFirst().orElse(null);
            if (psiElement != null) {
                targetElement = psiElement;
            }
        }

        // 找到引用的类和字段
        PsiClass psiClass = null;
        PsiReference psiReference = targetElement.getReference();
        PsiElement firstChild = targetElement.getFirstChild();
        if (firstChild != null && !(firstChild instanceof PsiJavaCodeReferenceElement)) {
            firstChild = firstChild.getFirstChild();
        }
        if ((firstChild instanceof PsiJavaCodeReferenceElement) && firstChild.getReference() != null) {
            psiClass = PsiUtils.findPsiClass(project, module, firstChild.getReference().getCanonicalText());
        }
        if (psiClass == null) {
            return Collections.emptyList();
        }

        // 枚举类
        boolean isEnum = psiClass.isEnum();
        if (isEnum) {
            if (psiReference == null) {
                return getEnumValues(psiClass);
            } else {
                String fieldName = psiReference.getCanonicalText();
                PsiField field = Arrays.stream(psiClass.getFields())
                        .filter(one -> one.getName().equals(fieldName))
                        .findFirst().orElse(null);
                if (field == null) {
                    return Collections.emptyList();
                }
                if (field instanceof PsiEnumConstant) {
                    return Lists.newArrayList(new Value(field.getName(), PsiDocCommentUtils.getDocCommentTitle(field)));
                }

                // 解析枚举表达式
                int fieldIndex = -1;
                for (PsiMethod constructor : psiClass.getConstructors()) {
                    JvmParameter[] parameters = constructor.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].getName().equals(fieldName)) {
                            fieldIndex = i;
                        }
                    }
                }
                if (fieldIndex == -1) {
                    return Collections.emptyList();
                }

                PsiField[] enumFields = PsiUtils.getEnumFields(psiClass);
                List<Value> values = Lists.newArrayListWithExpectedSize(enumFields.length);
                for (PsiField enumField : enumFields) {
                    PsiElement expressionList = Arrays.stream(enumField.getChildren())
                            .filter(e -> e instanceof PsiExpressionList)
                            .findFirst().orElse(null);
                    if (expressionList == null) {
                        continue;
                    }
                    PsiLiteralExpression[] psiLiteralExpressions = Arrays.stream(expressionList.getChildren())
                            .filter(e -> e instanceof PsiLiteralExpression)
                            .toArray(PsiLiteralExpression[]::new);
                    if (psiLiteralExpressions.length > fieldIndex) {
                        String value = psiLiteralExpressions[fieldIndex].getText();
                        String description = PsiDocCommentUtils.getDocCommentTitle(enumField);
                        if (StringUtils.isEmpty(description)) {
                            description = enumField.getName();
                        }
                        values.add(new Value(value, description));
                    }
                }
                return values;
            }
        }

        // 常量类
        PsiField[] fields = PsiUtils.getStaticOrFinalFields(psiClass);
        if (psiReference != null) {
            fields = Arrays.stream(fields)
                    .filter(f -> f.getName().equals(psiReference.getCanonicalText()))
                    .toArray(PsiField[]::new);
        }
        List<Value> values = Lists.newArrayListWithExpectedSize(fields.length);
        for (PsiField f : fields) {
            String value = PsiFieldUtils.getFieldDeclaredValue(f);
            if (value == null) {
                continue;
            }
            String description = PsiDocCommentUtils.getDocCommentTitle(f);
            values.add(new Value(value, description));
        }
        return values;
    }

    /**
     * 是否标记过期
     */
    public boolean getFieldDeprecated(PsiField field) {
        PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(field, Deprecated.class.getName());
        if (annotation != null) {
            return true;
        }
        PsiDocTag deprecatedTag = PsiDocCommentUtils.findTagByName(field, Constants.DOC_TAG.DEPRECATED);
        return Objects.nonNull(deprecatedTag);
    }


    /**
     * 字段是否被跳过
     */
    public boolean isFieldIgnore(PsiField field) {
        // swagger -> @ignore -> @JsonIgnore
        if (PsiSwaggerUtils.isFieldIgnore(field)) {
            return true;
        }

        PsiDocTag ignoreTag = PsiDocCommentUtils.findTagByName(field, Constants.DOC_TAG.IGNORE);
        if (ignoreTag != null) {
            return true;
        }

        String jsonIgnore = PsiAnnotationUtils.getFieldAnnotationStringAttributeValue(field, ExtraPackageNameEnum.JSON_IGNORE.getName(), "value", true);
        if ("true".equals(jsonIgnore)) {
            return true;
        }
        return false;
    }
    //----------------------------- 类型 -----------------------------//

    public String getTypeDescription(PsiType type, List<Value> values) {
        if (values != null && !values.isEmpty()) {
            return values.stream().map(Value::getText).collect(Collectors.joining(", "));
        } else if (type != null) {
            return type.getPresentableText();
        }
        return null;
    }

    /**
     * 获取指定类型可能的值
     */
    public List<Value> getTypeValues(PsiType psiType) {
        boolean isEnum = PsiTypeUtils.isEnum(psiType);
        if (isEnum) {
            PsiClass enumPsiClass = PsiTypeUtils.getEnumClassIncludeArray(this.project, this.module, psiType);
            if (enumPsiClass != null) {
                return getEnumValues(enumPsiClass);
            }
        }
        return null;
    }

    /**
     * 获取未忽略的字段
     */
    public List<PsiField> getFields(PsiClass psiClass) {
        PsiField[] fields = PsiUtils.getFields(psiClass);
        List<String> includeProperties = PsiAnnotationUtils.getStringArrayAttribute(psiClass, ExtraPackageNameEnum.JSON_INCLUDE_PROPERTIES.getName(), "value");
        if (includeProperties != null) {
            Set<String> includePropertiesSet = Sets.newHashSet(includeProperties);
            return Arrays.stream(fields)
                    .filter(filed -> includePropertiesSet.contains(filed.getName()))
                    .filter(field -> !isFieldIgnore(field))
                    .collect(Collectors.toList());
        }

        List<String> ignoreProperties = PsiAnnotationUtils.getStringArrayAttribute(psiClass, ExtraPackageNameEnum.JSON_IGNORE_PROPERTIES.getName(), "value");
        if (ignoreProperties != null && !ignoreProperties.isEmpty()) {
            Set<String> ignorePropertiesSet = Sets.newHashSet(ignoreProperties);
            return Arrays.stream(fields)
                    .filter(filed -> !ignorePropertiesSet.contains(filed.getName()))
                    .filter(field -> !isFieldIgnore(field))
                    .collect(Collectors.toList());
        }

        return Arrays.stream(fields)
                .filter(field -> !isFieldIgnore(field))
                .collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Jsr303Info getJsr303Info(PsiModifierListOwner element) {
        Jsr303Info data = new Jsr303Info();
        // @Size
        PsiAnnotation sizeAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.SIZE.getName(), ExtraPackageNameEnum.SIZE2.getName());
        if (sizeAnnotation != null) {
            Integer minSize = PsiAnnotationUtils.getIntegerAttributeValueByAnnotation(sizeAnnotation, Constants.ANNOTATION_ATTR.MIN);
            Integer maxSize = PsiAnnotationUtils.getIntegerAttributeValueByAnnotation(sizeAnnotation, Constants.ANNOTATION_ATTR.MAX);
            data.setMinLength(minSize);
            data.setMaxLength(maxSize);
        }

        // @Min, @DecimalMin, @Positive, @PositiveOrZero
        BigDecimal minValue = null, decimalMinValue = null, positiveValue = null, positiveOrZeroValue = null;
        PsiAnnotation minAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.MIN.getName(), ExtraPackageNameEnum.MIN2.getName());
        if (minAnnotation != null) {
            minValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(minAnnotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        PsiAnnotation decimalMinAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.DECIMAL_MIN.getName(), ExtraPackageNameEnum.DECIMAL_MIN2.getName());
        if (decimalMinAnnotation != null) {
            decimalMinValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(decimalMinAnnotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        PsiAnnotation positiveAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.POSITIVE.getName(), ExtraPackageNameEnum.POSITIVE2.getName());
        if (positiveAnnotation != null) {
            positiveValue = BigDecimal.ZERO;
        }
        PsiAnnotation positiveOrZeroAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.POSITIVE_OR_ZERO.getName(), ExtraPackageNameEnum.POSITIVE_OR_ZERO2.getName());
        if (positiveOrZeroAnnotation != null) {
            positiveOrZeroValue = BigDecimal.ZERO;
        }
        BigDecimal min = Stream.of(minValue, decimalMinValue, positiveValue, positiveOrZeroValue)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(null);
        data.setMinimum(min);

        // @Max, @DecimalMax, @Negative, @NegativeOrZero
        BigDecimal maxValue = null, decimalMaxValue = null, negativeValue = null, negativeOrZeroValue = null;
        PsiAnnotation maxAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.MAX.getName(), ExtraPackageNameEnum.MAX2.getName());
        if (maxAnnotation != null) {
            maxValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(maxAnnotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        PsiAnnotation decimalMaxAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.DECIMAL_MAX.getName(), ExtraPackageNameEnum.DECIMAL_MAX2.getName());
        if (decimalMaxAnnotation != null) {
            decimalMaxValue = PsiAnnotationUtils.getBigDecimalAttributeValueByAnnotation(decimalMaxAnnotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        PsiAnnotation negativeAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.NEGATIVE.getName(), ExtraPackageNameEnum.NEGATIVE2.getName());
        if (negativeAnnotation != null) {
            negativeValue = BigDecimal.ZERO;
        }
        PsiAnnotation negativeOrZeroAnnotation = PsiAnnotationUtils.getAnnotation(element, ExtraPackageNameEnum.NEGATIVE_OR_ZERO.getName(), ExtraPackageNameEnum.NEGATIVE_OR_ZERO2.getName());
        if (negativeOrZeroAnnotation != null) {
            negativeOrZeroValue = BigDecimal.ZERO;
        }
        BigDecimal max = Stream.of(maxValue, decimalMaxValue, negativeValue, negativeOrZeroValue)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(null);
        data.setMaximum(max);

        return data;
    }

    /**
     * 方法是否被忽略: JavaDoc是否有ignore标签/Swagger是否有@ApiIgnore注解
     *
     * @param method 方法
     * @return boolean
     * @author mabin
     * @date 2024/05/13 14:28
     */
    public boolean isMethodIgnored(PsiMethod method) {
        PsiDocTag ignoreTag = PsiDocCommentUtils.findTagByName(method, Constants.DOC_TAG.IGNORE);
        if (Objects.nonNull(ignoreTag)) {
            return true;
        }
        PsiAnnotation ignoreAnnotation = PsiAnnotationUtils.getAnnotation(method, SwaggerAnnotationEnum.API_IGNORE.getClassPackage());
        return Objects.nonNull(ignoreAnnotation);
    }

    /**
     * 是否忽略类: JavaDoc是否有ignore标签/Swagger是否有@ApiIgnore注解
     *
     * @param psiClass psi级
     * @return boolean
     * @author mabin
     * @date 2024/05/13 14:29
     */
    public boolean isClassIgnored(PsiClass psiClass) {
        PsiDocTag ignoreTag = PsiDocCommentUtils.findTagByName(psiClass, Constants.DOC_TAG.IGNORE);
        if (Objects.nonNull(ignoreTag)) {
            return true;
        }
        PsiAnnotation ignoreAnnotation = PsiAnnotationUtils.getAnnotation(psiClass, SwaggerAnnotationEnum.API_IGNORE.getClassPackage());
        return Objects.nonNull(ignoreAnnotation);
    }

}

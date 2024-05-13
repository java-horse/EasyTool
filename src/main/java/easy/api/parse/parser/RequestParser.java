package easy.api.parse.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import easy.api.config.ApidocxConfig;
import easy.api.config.ApidocxConfig.RequestBodyParamType;
import easy.api.model.common.*;
import easy.api.parse.model.Jsr303Info;
import easy.api.parse.model.RequestInfo;
import easy.api.parse.model.TypeParseContext;
import easy.api.parse.util.PsiAnnotationUtils;
import easy.api.parse.util.PsiDocCommentUtils;
import easy.api.parse.util.PsiTypeUtils;
import easy.base.ApiDocConstants;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SpringAnnotationEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 请求信息解析
 *
 * @see #parse(PsiMethod, HttpMethod)
 */
public class RequestParser {

    private final ApidocxConfig settings;
    private final KernelParser kernelParser;
    private final ParseHelper parseHelper;
    private final DateParser dateParser;

    public RequestParser(Project project, Module module, ApidocxConfig settings) {
        this.settings = settings;
        this.kernelParser = new KernelParser(project, module, settings, false);
        this.dateParser = new DateParser(settings);
        this.parseHelper = new ParseHelper(project, module);
    }

    /**
     * 解析请求参数信息
     *
     * @param method     待处理的方法
     * @param httpMethod 当前方法的http请求方法
     */
    public RequestInfo parse(PsiMethod method, HttpMethod httpMethod) {
        RequestInfo request = new RequestInfo();
        List<PsiParameter> parameters = filterMethodParameters(method);
        if (CollectionUtils.isEmpty(parameters)) {
            return request;
        }
        // 解析参数: 请求体类型，普通参数，请求体
        List<Property> requestParameters = getRequestParameters(method, parameters);
        RequestBodyType requestBodyType = getRequestBodyType(parameters, httpMethod);
        List<Property> requestBody = getRequestBody(method, parameters, httpMethod, requestParameters);
        request.setParameters(requestParameters);
        request.setRequestBodyType(requestBodyType);
        request.setRequestBodyForm(Collections.emptyList());
        if (requestBodyType != null && requestBodyType.isFormOrFormData()) {
            request.setRequestBodyForm(requestBody);
        } else if (!requestBody.isEmpty()) {
            request.setRequestBody(requestBody.get(0));
        }
        return request;
    }

    /**
     * 解析普通参数
     */
    public List<Property> getRequestParameters(PsiMethod method, List<PsiParameter> parameterList) {
        List<PsiParameter> parameters = filterRequestParameters(parameterList);
        return parameters.stream().flatMap(p -> {
            Property property = doParseParameter(method, p);
            List<Property> properties = flatProperty(property);
            properties.forEach(this::doSetPropertyDateFormat);
            return properties.stream();
        }).collect(Collectors.toList());
    }


    /**
     * 解析请求方式
     */
    private RequestBodyType getRequestBodyType(List<PsiParameter> parameters, HttpMethod method) {
        if (!method.isAllowBody()) {
            return null;
        }
        boolean requestBody = parameters.stream().anyMatch(p -> p.getAnnotation(SpringAnnotationEnum.REQUEST_BODY_TEXT.getName()) != null);
        if (requestBody) {
            return RequestBodyType.json;
        }
        List<ParameterAnnotationPair> requestBodyParamParameters = getRequestBodyParamParameters(parameters);
        if (!requestBodyParamParameters.isEmpty()) {
            return RequestBodyType.json;
        }

        boolean multipart = parameters.stream().anyMatch(p -> PsiTypeUtils.isFileIncludeArray(p.getType()));
        if (multipart) {
            return RequestBodyType.form_data;
        }
        return RequestBodyType.form;
    }

    /**
     * 解析请求体内容
     */
    private List<Property> getRequestBody(PsiMethod method, List<PsiParameter> methodParameters, HttpMethod httpMethod,
                                          List<Property> requestParameters) {
        if (!httpMethod.isAllowBody()) {
            return Lists.newArrayList();
        }
        Map<String, String> paramTags = PsiDocCommentUtils.getTagParamTextMap(method);
        Property bodyProperty = null;

        // JSON: 解析@RequestBody注解参数、自定义@RequestBody注解参数
        PsiParameter bodyParameter = methodParameters.stream()
                .filter(p -> p.getAnnotation(SpringAnnotationEnum.REQUEST_BODY_TEXT.getName()) != null).findFirst().orElse(null);
        if (bodyParameter != null) {
            bodyProperty = kernelParser.parse(bodyParameter.getType());
            String description = paramTags.get(bodyParameter.getName());
            if (StringUtils.isNotEmpty(description)) {
                bodyProperty.setDescription(description);
            }
        }
        List<ParameterAnnotationPair> bodyParamParameters = getRequestBodyParamParameters(methodParameters);
        if (!bodyParamParameters.isEmpty()) {
            if (bodyProperty == null) {
                bodyProperty = new Property();
                bodyProperty.setRequired(false);
                bodyProperty.setType(DataTypes.OBJECT);
            }
            for (ParameterAnnotationPair pair : bodyParamParameters) {
                PsiParameter parameter = pair.getParameter();
                PsiAnnotation annotation = pair.getAnnotation();

                Property property = kernelParser.parse(parameter.getType());
                property.setRequired(true);
                property.setDescription(paramTags.get(parameter.getName()));
                String name = PsiAnnotationUtils.getStringAttributeValueByAnnotation(annotation,
                        settings.getRequestBodyParamType().getProperty());
                if (StringUtils.isEmpty(name)) {
                    name = parameter.getName();
                }
                bodyProperty.addProperty(name, property);
            }
        }
        if (bodyProperty != null) {
            return Lists.newArrayList(bodyProperty);
        }
        // 2.文件类型
        List<Property> formProperties = Lists.newArrayList();
        List<PsiParameter> fileParameters = methodParameters.stream()
                .filter(p -> PsiTypeUtils.isFileIncludeArray(p.getType())).collect(Collectors.toList());
        for (PsiParameter p : fileParameters) {
            Property item = kernelParser.parse(p.getType());
            item.setType(DataTypes.FILE);
            item.setFormat("binary");
            item.setName(p.getName());
            item.setRequired(true);
            item.setDescription(paramTags.get(p.getName()));
            formProperties.add(item);
        }
        // 表单：查询参数合并查询参数到表单
        if (requestParameters != null) {
            List<Property> queries = requestParameters.stream()
                    .filter(p -> p.getIn() == ParameterIn.query).collect(Collectors.toList());
            for (Property query : queries) {
                query.setIn(null);
                formProperties.add(query);
            }
            requestParameters.removeAll(queries);
        }
        return formProperties;
    }

    /**
     * 获取请求参数注解了自定义@RequestBody的参数
     */
    private List<ParameterAnnotationPair> getRequestBodyParamParameters(List<PsiParameter> parameters) {
        // 自定义@RequestBody类型
        RequestBodyParamType type = settings.getRequestBodyParamType();
        if (type == null) {
            return Collections.emptyList();
        }

        List<ParameterAnnotationPair> pairs = new ArrayList<>();
        for (PsiParameter p : parameters) {
            PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(p, type.getAnnotation());
            if (annotation != null) {
                pairs.add(new ParameterAnnotationPair(p, annotation));
            }
        }
        return pairs;
    }

    private void doSetPropertyDateFormat(Property item) {
        // 附加时间格式
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(item.getDescription())) {
            sb.append(item.getDescription());
        }
        if (StringUtils.isNotEmpty(item.getDateFormat())) {
            if (!sb.isEmpty()) {
                sb.append(" : ");
            }
            sb.append(item.getDateFormat());
        }
        item.setDescription(sb.toString());
    }

    /**
     * 解析单个参数
     */
    private Property doParseParameter(PsiMethod method, PsiParameter parameter) {
        // 解析类型中的信息
        TypeParseContext context = createTypeParseContext(method, parameter);
        Property property = kernelParser.parse(context, parameter.getType(), parameter.getType().getCanonicalText());
        // 方法参数级别信息处理
        dateParser.handle(property, parameter);
        PsiAnnotation annotation = null;
        ParameterIn in = ParameterIn.query;
        Map<String, ParameterIn> targets = new LinkedHashMap<>();
        targets.put(SpringAnnotationEnum.REQUEST_PARAM_TEXT.getName(), ParameterIn.query);
        targets.put(SpringAnnotationEnum.REQUEST_ATTRIBUTE.getName(), ParameterIn.query);
        targets.put(SpringAnnotationEnum.REQUEST_HEADER_TEXT.getName(), ParameterIn.header);
        targets.put(SpringAnnotationEnum.PATH_VARIABLE_TEXT.getName(), ParameterIn.path);
        for (Entry<String, ParameterIn> target : targets.entrySet()) {
            annotation = PsiAnnotationUtils.getAnnotation(parameter, target.getKey());
            if (annotation != null) {
                in = target.getValue();
                break;
            }
        }
        // 字段名称
        Boolean required = null;
        String name = null;
        String defaultValue = null;
        if (annotation != null) {
            name = PsiAnnotationUtils.getStringAttributeValueByAnnotation(annotation, Constants.ANNOTATION_ATTR.NAME);
            if (StringUtils.isBlank(name)) {
                name = PsiAnnotationUtils.getStringAttributeValueByAnnotation(annotation, Constants.ANNOTATION_ATTR.VALUE);
                if (StringUtils.isBlank(name)) {
                    name = parameter.getName();
                }
            }
            required = AnnotationUtil.getBooleanAttributeValue(annotation, Constants.ANNOTATION_ATTR.REQUIRED);
            if (required == null) {
                required = parseHelper.getParameterRequired(parameter);
            }
            defaultValue = PsiAnnotationUtils.getStringAttributeValueByAnnotation(annotation, Constants.ANNOTATION_ATTR.DEFAULT_VALUE);
            if (ApiDocConstants.DEFAULT_NONE.equals(defaultValue)) {
                defaultValue = null;
            }
        }
        Map<String, String> paramTags = PsiDocCommentUtils.getTagParamTextMap(method);
        property.setIn(in);
        property.setName(name);
        property.setDescription(parseHelper.getParameterDescription(method, parameter, paramTags, property.getPropertyValues()));
        property.setRequired(required != null ? required : false);
        property.setDefaultValue(defaultValue);
        // JSR303注解
        Jsr303Info jsr303Info = parseHelper.getJsr303Info(parameter);
        if (jsr303Info.getMinLength() != null) {
            property.setMinLength(jsr303Info.getMinLength());
        }
        if (jsr303Info.getMaxLength() != null) {
            property.setMaxLength(jsr303Info.getMaxLength());
        }
        if (jsr303Info.getMinimum() != null) {
            property.setMinimum(jsr303Info.getMinimum());
        }
        if (jsr303Info.getMaximum() != null) {
            property.setMaximum(jsr303Info.getMaximum());
        }
        return property;
    }

    @NotNull
    private static TypeParseContext createTypeParseContext(PsiMethod method, PsiParameter parameter) {
        PsiAnnotation validatedAnnotation = PsiAnnotationUtils.getAnnotation(parameter, ExtraPackageNameEnum.VALIDATED.getName());
        if (validatedAnnotation == null) {
            validatedAnnotation = PsiAnnotationUtils.getAnnotation(method, ExtraPackageNameEnum.VALIDATED.getName());
        }
        List<String> validatedClasses = Lists.newArrayList();
        if (validatedAnnotation != null) {
            validatedClasses = PsiAnnotationUtils.getStringArrayAttribute(validatedAnnotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        TypeParseContext context = new TypeParseContext();
        context.setJsr303ValidateGroups(validatedClasses);
        return context;
    }


    /**
     * 过滤无需处理的参数
     */
    private List<PsiParameter> filterMethodParameters(PsiMethod method) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        Set<String> ignoreTypes = Sets.newHashSet(settings.getParameterIgnoreTypes());
        return Arrays.stream(parameters)
                .filter(p -> {
                    String type = p.getType().getCanonicalText();
                    return !ignoreTypes.contains(type);
                }).collect(Collectors.toList());
    }

    private List<PsiParameter> filterRequestParameters(List<PsiParameter> parameters) {
        return parameters.stream()
                .filter(p -> p.getAnnotation(SpringAnnotationEnum.REQUEST_BODY_TEXT.getName()) == null)
                .filter(p -> {
                    // 过滤掉自定义@RequestBody类型的参数
                    RequestBodyParamType requestBodyParamType = settings.getRequestBodyParamType();
                    if (requestBodyParamType == null) {
                        return true;
                    }
                    return PsiAnnotationUtils.getAnnotation(p, requestBodyParamType.getAnnotation()) == null;
                })
                .filter(p -> !PsiTypeUtils.isFileIncludeArray(p.getType()))
                .collect(Collectors.toList());
    }

    /**
     * 解析Item为扁平结构的parameter
     */
    private List<Property> flatProperty(Property property) {
        if (property == null) {
            return Collections.emptyList();
        }
        boolean flat = property.isObjectType() && property.getProperties() != null && ParameterIn.query == property.getIn();
        if (flat) {
            Collection<Property> properties = property.getProperties().values();
            properties.forEach(one -> one.setIn(property.getIn()));
            return Lists.newArrayList(properties);
        }
        return Lists.newArrayList(property);
    }

    private static class ParameterAnnotationPair {
        private PsiParameter parameter;
        private PsiAnnotation annotation;

        public ParameterAnnotationPair(PsiParameter parameter, PsiAnnotation annotation) {
            this.parameter = parameter;
            this.annotation = annotation;
        }

        public PsiParameter getParameter() {
            return parameter;
        }

        public void setParameter(PsiParameter parameter) {
            this.parameter = parameter;
        }

        public PsiAnnotation getAnnotation() {
            return annotation;
        }

        public void setAnnotation(PsiAnnotation annotation) {
            this.annotation = annotation;
        }
    }


}

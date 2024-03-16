package easy.doc.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import easy.config.doc.JavaDocTemplateConfig.CustomValue;
import easy.doc.variable.VariableGenerator;
import easy.doc.variable.impl.*;
import easy.enums.JavaDocInnerVariableEnum;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JavaDocVariableGeneratorService {
    private static final Logger log = Logger.getInstance(JavaDocVariableGeneratorService.class);
    private final Pattern pattern = Pattern.compile("\\$[a-zA-Z0-9_-]*\\$");

    private final Map<String, VariableGenerator> variableGeneratorMap = ImmutableMap.<String, VariableGenerator>builder()
            .put(JavaDocInnerVariableEnum.AUTHOR.key, new AuthorVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.DATE.key, new DateVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.DOC.key, new DocVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.PARAMS.key, new ParamsVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.RETURN.key, new ReturnVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.SEE.key, new SeeVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.SINCE.key, new SinceVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.THROWS.key, new ThrowsVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.VERSION.key, new VersionVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.PROJECT.key, new ProjectVariableGeneratorImpl())
            .put(JavaDocInnerVariableEnum.PACKAGE.key, new PackageVariableGeneratorImpl())
            .build();

    public String generate(PsiElement psiElement, String template, Map<String, CustomValue> customValueMap, Map<String, Object> innerVariableMap) {
        if (StringUtils.isBlank(template)) {
            return StringUtils.EMPTY;
        }
        // 匹配占位符
        Matcher matcher = pattern.matcher(template);
        Map<String, String> variableMap = Maps.newLinkedHashMap();
        while (matcher.find()) {
            String placeholder = matcher.group();
            String key = StringUtils.substring(placeholder, 1, -1);
            if (StringUtils.isBlank(key)) {
                return StringUtils.EMPTY;
            }
            VariableGenerator variableGenerator = variableGeneratorMap.get(key.toLowerCase());
            if (Objects.isNull(variableGenerator)) {
                variableMap.put(placeholder, generateCustomVariable(customValueMap, innerVariableMap, placeholder));
            } else {
                variableMap.put(placeholder, variableGenerator.generate(psiElement));
            }
        }
        // 占位符替换
        List<String> keyList = Lists.newArrayList();
        List<String> valueList = Lists.newArrayList();
        for (Entry<String, String> entry : variableMap.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }
        return StringUtils.replaceEach(template, keyList.toArray(new String[0]), valueList.toArray(new String[0]));
    }

    private String generateCustomVariable(Map<String, CustomValue> customValueMap, Map<String, Object> innerVariableMap, String placeholder) {
        Optional<CustomValue> valueOptional = customValueMap.entrySet().stream()
                .filter(entry -> placeholder.equalsIgnoreCase(entry.getKey())).map(Entry::getValue).findAny();
        // 找不到自定义方法，返回原占位符
        if (valueOptional.isEmpty()) {
            return placeholder;
        }
        CustomValue value = valueOptional.get();
        switch (value.getJavaDocVariableTypeEnum()) {
            case STRING:
                return value.getValue();
            case GROOVY:
                try {
                    return new GroovyShell(new Binding(innerVariableMap)).evaluate(value.getValue()).toString();
                } catch (Exception e) {
                    log.error(String.format("自定义变量%s的groovy脚本执行异常，请检查语法是否正确且有正确返回值:%s", placeholder, value.getValue()), e);
                    return value.getValue();
                }
            default:
                return StringUtils.EMPTY;
        }
    }

}

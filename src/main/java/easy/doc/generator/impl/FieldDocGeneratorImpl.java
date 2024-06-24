package easy.doc.generator.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.doc.service.JavaDocVariableGeneratorService;
import easy.enums.JavaDocPropertyCommentModelEnum;
import easy.enums.JavaDocPropertyCommentTypeEnum;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import easy.util.VcsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FieldDocGeneratorImpl extends AbstractDocGenerator {
    private static final Logger log = Logger.getInstance(MethodDocGeneratorImpl.class);
    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);
    private JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private JavaDocVariableGeneratorService javaDocVariableGeneratorService = ServiceHelper.getService(JavaDocVariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiField psiField)) {
            return StringUtils.EMPTY;
        }
        JavaDocTemplateConfig javaDocFieldTemplateConfig = javaDocConfig.getJavaDocFieldTemplateConfig();
        if (Objects.nonNull(javaDocFieldTemplateConfig) && Boolean.TRUE.equals(javaDocFieldTemplateConfig.getIsDefault())) {
            return defaultGenerate(psiField);
        } else {
            return customGenerate(psiField);
        }
    }

    /**
     * 默认的生成
     *
     * @param psiField 当前属性
     * @return {@link String}
     */
    private String defaultGenerate(PsiField psiField) {
        if (StringUtils.equals(JavaDocPropertyCommentTypeEnum.SINGLE.getType(), javaDocConfig.getPropertyCommentType())) {
            return genSimpleDoc(psiField, psiField.getName());
        } else {
            return genNormalDoc(psiField, psiField.getName());
        }
    }

    /**
     * 生成正常的文档
     *
     * @param psiField 属性
     * @param name     名字
     * @return {@link String}
     */
    private String genNormalDoc(PsiField psiField, String name) {
        if (StringUtils.equals(JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel(), javaDocConfig.getPropertyCommentModel())) {
            PsiDocComment comment = psiField.getDocComment();
            if (comment != null) {
                List<PsiElement> elements = Lists.newArrayList(comment.getChildren());
                String desc = translateService.translate(name);
                List<String> commentItems = Lists.newLinkedList();
                for (PsiElement element : elements) {
                    commentItems.add(element.getText());
                }
                commentItems.add(1, buildDesc(elements, desc));
                return Joiner.on(StringUtils.EMPTY).skipNulls().join(commentItems);
            }
            return String.format("/**%s* %s%s */", StringUtils.LF, translateService.translate(name), StringUtils.LF);
        } else {
            // 只能兼容字段上方紧跟注释的情况（中间有空行时无效）
            if (Arrays.stream(StringUtils.split(psiField.getText(), StringUtils.LF)).map(StringUtils::trim).anyMatch(item -> StringUtils.startsWith(item, "/*") && StringUtils.endsWith(item, "*/"))) {
                return null;
            }
            return String.format("/* %s */", translateService.translate(name));
        }
    }

    /**
     * 生成简单的文档
     *
     * @param name 名字
     * @return {@link String}
     */
    private String genSimpleDoc(PsiField psiField, String name) {
        if (StringUtils.equals(JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel(), javaDocConfig.getPropertyCommentModel())) {
            PsiDocComment comment = psiField.getDocComment();
            if (comment != null) {
                for (PsiElement element : comment.getChildren()) {
                    if (!"PsiDocToken:DOC_COMMENT_DATA".equalsIgnoreCase(element.toString())) {
                        continue;
                    }
                    String source = element.getText().replaceAll("[/* \n]+", StringUtils.EMPTY);
                    if (StringUtils.isNotBlank(source)) {
                        return null;
                    }
                }
            }
            return String.format("/** %s */", translateService.translate(name));
        } else {
            // 只能兼容字段上方紧跟注释的情况（中间有空行时无效）
            if (Arrays.stream(StringUtils.split(psiField.getText(), StringUtils.LF)).map(StringUtils::trim).anyMatch(item -> StringUtils.startsWith(item, "//"))) {
                return null;
            }
            return String.format("// %s", translateService.translate(name));
        }
    }

    /**
     * 构建描述
     *
     * @param elements 元素
     * @param desc     描述
     * @return {@link String}
     */
    private String buildDesc(List<PsiElement> elements, String desc) {
        for (PsiElement element : elements) {
            if (!"PsiDocToken:DOC_COMMENT_DATA".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String source = element.getText().replaceAll("[/* \n]+", StringUtils.EMPTY);
            if (StringUtils.isNotBlank(source)) {
                return null;
            }
        }
        return desc;
    }

    private String customGenerate(PsiField psiField) {
        JavaDocTemplateConfig javaDocFieldTemplateConfig = javaDocConfig.getJavaDocFieldTemplateConfig();
        String doc = javaDocVariableGeneratorService.generate(psiField, javaDocFieldTemplateConfig.getTemplate(), javaDocFieldTemplateConfig.getCustomMap(), getFieldInnerVariable(psiField));
        return mergeDoc(psiField, doc);
    }

    private Map<String, Object> getFieldInnerVariable(PsiField psiField) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", javaDocConfig.getAuthor());
        map.put("fieldName", psiField.getName());
        map.put("fieldType", psiField.getType().getCanonicalText());
        map.put("branch", VcsUtil.getCurrentBranch(psiField.getProject()));
        return map;
    }

}

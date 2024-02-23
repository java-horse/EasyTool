package easy.doc.generator.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.generator.DocGenerator;
import easy.enums.JavaDocPropertyCommentTypeEnum;
import easy.service.TranslateService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FieldDocGeneratorImpl implements DocGenerator {
    private static final Logger log = Logger.getInstance(MethodDocGeneratorImpl.class);
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);
    private JavaDocConfig javaDocConfig = ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiField)) {
            return StringUtils.EMPTY;
        }
        PsiField psiField = (PsiField) psiElement;
        return defaultGenerate(psiField);
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
        PsiDocComment comment = psiField.getDocComment();
        if (comment != null) {
            List<PsiElement> elements = Lists.newArrayList(comment.getChildren());
            // 注释
            String desc = translateService.translate(name);
            List<String> commentItems = Lists.newLinkedList();
            for (PsiElement element : elements) {
                commentItems.add(element.getText());
            }
            commentItems.add(1, buildDesc(elements, desc));
            return Joiner.on(StringUtils.EMPTY).skipNulls().join(commentItems);
        }
        return String.format("/**%s* %s%s */", "\n", translateService.translate(name), "\n");
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

    /**
     * 生成简单的文档
     *
     * @param name 名字
     * @return {@link String}
     */
    private String genSimpleDoc(PsiField psiField, String name) {
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
    }

}

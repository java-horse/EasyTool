package easy.doc.generator.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.generator.DocGenerator;
import easy.service.TranslateService;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ClassDocGeneratorImpl implements DocGenerator {
    private static final Logger LOGGER = Logger.getInstance(ClassDocGeneratorImpl.class);
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);
    private JavaDocConfig javaDocConfig = ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiClass)) {
            return StringUtils.EMPTY;
        }
        PsiClass psiClass = (PsiClass) psiElement;
        return defaultGenerate(psiClass);
    }

    private String defaultGenerate(PsiClass psiClass) {
        String dateString;
        try {
            dateString = DateUtil.format(new Date(), javaDocConfig.getDateFormat());
        } catch (Exception e) {
            // todo 错误配置弹窗通知(错误描述, 跳转设置页面的action按钮)
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！");
            dateString = DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
        // 有注释，进行兼容处理
        if (psiClass.getDocComment() != null) {
            List<PsiElement> elements = Lists.newArrayList(psiClass.getDocComment().getChildren());
            List<String> startList = Lists.newArrayList();
            List<String> endList = Lists.newArrayList();
            // 注释
            startList.add(buildDesc(elements, translateService.translate(psiClass.getName())));
            // 作者
            endList.add(buildAuthor(elements));
            // 日期
            endList.add(buildDate(elements));

            List<String> commentItems = Lists.newLinkedList();
            for (PsiElement element : elements) {
                commentItems.add(element.getText());
            }
            for (String s : startList) {
                commentItems.add(1, s);
            }
            for (String s : endList) {
                commentItems.add(commentItems.size() - 1, s);
            }
            return Joiner.on(StringUtils.EMPTY).skipNulls().join(commentItems);
        }
        return "/**\n"
                + "* " + translateService.translate(psiClass.getName()) + "\n"
                + "*\n"
                + "* @author " + javaDocConfig.getAuthor() + "\n"
                + "* @date " + dateString + "\n"
                + "*/";
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
     * 构建作者
     *
     * @param elements 元素
     * @return {@link String}
     */
    private String buildAuthor(List<PsiElement> elements) {
        boolean isInsert = true;
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@author".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag) element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert) {
            return "@author " + javaDocConfig.getAuthor() + "\n";
        } else {
            return null;
        }
    }

    /**
     * 构建日期
     *
     * @param elements 元素
     * @return {@link String}
     */
    private String buildDate(List<PsiElement> elements) {
        String dateString;
        try {
            // todo 自定义配置的日期格式
            dateString = DateUtil.format(new Date(), javaDocConfig.getDateFormat());
        } catch (Exception e) {
            // todo 错误配置弹窗通知(错误描述, 跳转设置页面的action按钮)
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！");
            dateString = DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
        boolean isInsert = true;
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@date".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag) element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert) {
            return "@date " + dateString + "\n";
        } else {
            return null;
        }
    }

    private String genFormatDate() {
        try {
            return DateUtil.format(new Date(), javaDocConfig.getDateFormat());
        } catch (Exception e) {
            // todo 错误配置弹窗通知(错误描述, 跳转设置页面的action按钮)
            LOGGER.error("您配置的日期格式不正确，请到设置界面中修改！");
            return DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
    }

}

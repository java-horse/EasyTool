package easy.doc.generator.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.notification.NotificationType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.doc.service.JavaDocVariableGeneratorService;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import easy.util.VcsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ClassDocGeneratorImpl extends AbstractDocGenerator {
    private TranslateService translateService = ServiceHelper.getService(TranslateService.class);
    private JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private JavaDocVariableGeneratorService javaDocVariableGeneratorService = ServiceHelper.getService(JavaDocVariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiClass psiClass)) {
            return StringUtils.EMPTY;
        }
        JavaDocTemplateConfig javaDocClassTemplateConfig = javaDocConfig.getJavaDocClassTemplateConfig();
        if (Objects.nonNull(javaDocClassTemplateConfig) && Boolean.TRUE.equals(javaDocClassTemplateConfig.getIsDefault())) {
            return defaultGenerate(psiClass);
        } else {
            return customGenerate(psiClass);
        }
    }

    private String defaultGenerate(PsiClass psiClass) {
        if (psiClass.getDocComment() != null) {
            List<PsiElement> elements = Lists.newArrayList(psiClass.getDocComment().getChildren());
            List<String> startList = Lists.newArrayList();
            List<String> endList = Lists.newArrayList();
            startList.add(buildDesc(elements, translateService.translate(psiClass.getName())));
            endList.add(buildAuthor(elements));
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
                + "* " + translateService.translate(psiClass.getName()) + StringUtils.LF
                + "*\n"
                + "* @author " + javaDocConfig.getAuthor() + StringUtils.LF
                + "* @date " + genFormatDate() + StringUtils.LF
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
            return "@author " + javaDocConfig.getAuthor() + StringUtils.LF;
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
            return "@date " + genFormatDate() + StringUtils.LF;
        } else {
            return null;
        }
    }

    /**
     * 生成指定格式日期
     *
     * @return
     */
    private String genFormatDate() {
        try {
            return DateUtil.format(new Date(), javaDocConfig.getDateFormat());
        } catch (Exception e) {
            NotifyUtil.notify("您配置的日期格式【" + javaDocConfig.getDateFormat() + "】错误, 请及时修改!", NotificationType.ERROR, EasyCommonUtil.getPluginSettingAction());
            return DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
    }

    private String customGenerate(PsiClass psiClass) {
        JavaDocTemplateConfig javaDocClassTemplateConfig = javaDocConfig.getJavaDocClassTemplateConfig();
        String doc = javaDocVariableGeneratorService.generate(psiClass, javaDocClassTemplateConfig.getTemplate(), javaDocClassTemplateConfig.getCustomMap(), getClassInnerVariable(psiClass));
        return mergeDoc(psiClass, doc);
    }

    private Map<String, Object> getClassInnerVariable(PsiClass psiClass) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", javaDocConfig.getAuthor());
        map.put("className", psiClass.getQualifiedName());
        map.put("simpleClassName", psiClass.getName());
        VcsUtil.getVcsUser(psiClass.getProject());
        return map;
    }

}

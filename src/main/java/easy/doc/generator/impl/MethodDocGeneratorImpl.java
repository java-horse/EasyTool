package easy.doc.generator.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.config.doc.JavaDocTemplateConfig;
import easy.doc.service.JavaDocVariableGeneratorService;
import easy.enums.JavaDocMethodReturnTypeEnum;
import easy.translate.TranslateService;
import easy.util.EasyCommonUtil;
import easy.util.NotificationUtil;
import easy.util.VcsUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MethodDocGeneratorImpl extends AbstractDocGenerator {

    private static final Logger log = Logger.getInstance(MethodDocGeneratorImpl.class);
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);
    private JavaDocConfig javaDocConfig = ApplicationManager.getApplication().getService(JavaDocConfigComponent.class).getState();
    private JavaDocVariableGeneratorService javaDocVariableGeneratorService = ApplicationManager.getApplication().getService(JavaDocVariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod psiMethod)) {
            return StringUtils.EMPTY;
        }
        JavaDocTemplateConfig javaDocMethodTemplateConfig = javaDocConfig.getJavaDocMethodTemplateConfig();
        if (Objects.nonNull(javaDocMethodTemplateConfig) && Boolean.TRUE.equals(javaDocMethodTemplateConfig.getIsDefault())) {
            return defaultGenerate(psiMethod);
        } else {
            return customGenerate(psiMethod);
        }
    }

    private String defaultGenerate(PsiMethod psiMethod) {
        List<String> paramNameList = Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameter::getName).collect(Collectors.toList());
        PsiTypeElement returns = psiMethod.getReturnTypeElement() == null ? null : psiMethod.getReturnTypeElement();
        String returnName = returns == null ? StringUtils.EMPTY : returns.getType().getCanonicalText();
        List<PsiClassType> exceptionTypeList = Arrays.stream(psiMethod.getThrowsList().getReferencedTypes()).collect(Collectors.toList());
        // 有注释，进行兼容处理
        if (psiMethod.getDocComment() != null) {
            List<PsiElement> elements = Lists.newArrayList(psiMethod.getDocComment().getChildren());
            List<String> startList = Lists.newArrayList();
            List<String> endList = Lists.newArrayList();
            // 注释
            startList.add(buildDesc(elements, translateService.translate(psiMethod.getName())));
            // 参数
            endList.addAll(buildParams(elements, paramNameList));
            // 返回值
            endList.add(buildReturn(elements, returns));
            // 异常
            endList.addAll(buildException(elements, exceptionTypeList, psiMethod.getProject()));
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
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append("* ").append(translateService.translate(psiMethod.getName())).append(StringUtils.LF);
        sb.append("*\n");
        for (String paramName : paramNameList) {
            sb.append("* @param ").append(paramName).append(StringUtils.SPACE).append(translateService.translate(paramName)).append(StringUtils.LF);
        }
        if (!returnName.isEmpty() && !"void".equals(returnName)) {
            if (Constants.BASE_TYPE_SET.contains(returnName)) {
                sb.append("* @return ").append(returnName).append(StringUtils.LF);
            } else {
                if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.CODE.getType())) {
                    sb.append("* @return {@code ").append(returnName).append("}").append(StringUtils.LF);
                } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.LINK.getType())) {
                    sb.append(getLinkTypeReturnDoc(returnName));
                } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.COMMENT.getType())) {
                    sb.append("* @return ").append(translateService.translate(returnName)).append(StringUtils.LF);
                }
            }
        }
        for (PsiClassType exceptionType : exceptionTypeList) {
            sb.append("* @throws ").append(exceptionType.getName()).append(StringUtils.SPACE)
                    .append(translateService.translate(exceptionType.getName())).append(StringUtils.LF);
        }
        sb.append("*/");
        return sb.toString();
    }

    /**
     * 构建异常
     *
     * @param elements          元素
     * @param exceptionTypeList 异常类型数组
     * @return {@link List<String>}
     */
    private List<String> buildException(List<PsiElement> elements, List<PsiClassType> exceptionTypeList, Project project) {
        List<String> paramDocList = Lists.newArrayList();
        Set<String> exceptionNameSet = exceptionTypeList.stream().map(PsiClassType::getName).collect(Collectors.toSet());
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@throws".equalsIgnoreCase(element.toString())
                    && !"PsiDocTag:@exception".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String exceptionName = null;
            String exceptionData = null;
            for (PsiElement child : element.getChildren()) {
                if (StringUtils.isBlank(exceptionName) && "PsiElement(DOC_TAG_VALUE_ELEMENT)".equals(child.toString())) {
                    exceptionName = StringUtils.trim(child.getText());
                } else if (StringUtils.isBlank(exceptionData) && "PsiDocToken:DOC_COMMENT_DATA".equals(child.toString())) {
                    exceptionData = StringUtils.trim(child.getText());
                }
            }
            if (StringUtils.isBlank(exceptionName) || StringUtils.isBlank(exceptionData)) {
                iterator.remove();
                continue;
            }
            if (!exceptionNameSet.contains(exceptionName)) {
                iterator.remove();
                continue;
            }
            exceptionNameSet.remove(exceptionName);
            for (Iterator<PsiClassType> iter = exceptionTypeList.iterator(); iter.hasNext(); ) {
                PsiClassType psiClassType = iter.next();
                if (psiClassType.getName().equals(exceptionName)) {
                    iter.remove();
                }
            }
        }
        for (PsiClassType exceptionType : exceptionTypeList) {
            paramDocList.add("@throws " + exceptionType.getName() + StringUtils.SPACE + translateService.translate(exceptionType.getName()) + StringUtils.LF);
        }
        return paramDocList;
    }

    /**
     * 构建返回
     *
     * @param elements 元素
     * @param returns  返回名称
     * @return {@link String}
     */
    private String buildReturn(List<PsiElement> elements, PsiTypeElement returns) {
        boolean isInsert = true;
        if (returns == null) {
            return StringUtils.EMPTY;
        }
        String returnName = returns.getType().getCanonicalText();
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@return".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag) element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else if (ObjectUtils.isEmpty(returnName) || "void".equals(returnName)) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert && ObjectUtils.isNotEmpty(returnName) && !"void".equals(returnName)) {
            if (Constants.BASE_TYPE_SET.contains(returnName)) {
                return "@return " + returnName + StringUtils.LF;
            } else {
                if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.CODE.getType())) {
                    return "@return {@code " + returnName + "}\n";
                } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.LINK.getType())) {
                    return getLinkTypeReturnDoc(returnName);
                } else if (StringUtils.equals(javaDocConfig.getMethodReturnType(), JavaDocMethodReturnTypeEnum.COMMENT.getType())) {
                    return "* @return " + translateService.translate(returnName) + StringUtils.LF;
                }
            }
        }
        return null;
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
            NotificationUtil.notify("您配置的日期格式【" + javaDocConfig.getDateFormat() + "】错误, 请及时修改!", NotificationType.ERROR, EasyCommonUtil.getPluginSettingAction());
            return DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
    }

    /**
     * 获取link类型文档注释
     *
     * @param returnName 返回名
     * @return {@link String}
     */
    private String getLinkTypeReturnDoc(String returnName) {
        return String.format("* @return {@link %s }", returnName) + StringUtils.LF;
    }

    /**
     * 构建参数
     *
     * @param elements      元素
     * @param paramNameList 参数名称数组
     * @return {@link List< String>}
     */
    private List<String> buildParams(List<PsiElement> elements, List<String> paramNameList) {
        List<String> paramDocList = Lists.newArrayList();
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@param".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String paramName = null;
            String paramData = null;
            for (PsiElement child : element.getChildren()) {
                if (StringUtils.isBlank(paramName) && "PsiElement(DOC_PARAMETER_REF)".equals(child.toString())) {
                    paramName = StringUtils.trim(child.getText());
                } else if (StringUtils.isBlank(paramData) && "PsiDocToken:DOC_COMMENT_DATA".equals(child.toString())) {
                    paramData = StringUtils.trim(child.getText());
                }
            }
            if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramData)) {
                iterator.remove();
                continue;
            }
            if (!paramNameList.contains(paramName)) {
                iterator.remove();
                continue;
            }
            paramNameList.remove(paramName);
        }
        for (String paramName : paramNameList) {
            paramDocList.add("@param " + paramName + StringUtils.SPACE + translateService.translate(paramName) + StringUtils.LF);
        }
        return paramDocList;
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

    private String customGenerate(PsiMethod psiMethod) {
        JavaDocTemplateConfig javaDocMethodTemplateConfig = javaDocConfig.getJavaDocMethodTemplateConfig();
        String doc = javaDocVariableGeneratorService.generate(psiMethod, javaDocMethodTemplateConfig.getTemplate(), javaDocMethodTemplateConfig.getCustomMap(), getMethodInnerVariable(psiMethod));
        return mergeDoc(psiMethod, doc);
    }

    private Map<String, Object> getMethodInnerVariable(PsiMethod psiMethod) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", javaDocConfig.getAuthor());
        map.put("methodName", psiMethod.getName());
        map.put("methodReturnType", psiMethod.getReturnType() == null ? StringUtils.EMPTY : psiMethod.getReturnType().getCanonicalText());
        map.put("methodParamTypes",
                Arrays.stream(psiMethod.getTypeParameters()).map(PsiTypeParameter::getQualifiedName).toArray(String[]::new));
        map.put("methodParamNames",
                Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameter::getName).toArray(String[]::new));
        map.put("branch", VcsUtil.getCurrentBranch(psiMethod.getProject()));
        return map;
    }

}

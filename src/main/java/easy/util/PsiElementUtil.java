package easy.util;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.enums.SpringAnnotationEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PsiElementUtil {

    private PsiElementUtil() {
    }

    /**
     * 获取PSI元素名称标识符文本
     *
     * @param psiElement psi的元素
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/20 15:36
     */
    public static String getPsiElementNameIdentifierText(PsiElement psiElement) {
        if (Objects.isNull(psiElement)) {
            return StringUtils.EMPTY;
        }
        if (psiElement instanceof PsiClass psiClass) {
            PsiIdentifier nameIdentifier = psiClass.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiMethod psiMethod) {
            PsiIdentifier nameIdentifier = psiMethod.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else if (psiElement instanceof PsiField psiField) {
            return psiField.getNameIdentifier().getText();
        } else if (psiElement instanceof PsiParameter psiParameter) {
            PsiIdentifier nameIdentifier = psiParameter.getNameIdentifier();
            if (Objects.isNull(nameIdentifier)) {
                return StringUtils.EMPTY;
            }
            return nameIdentifier.getText();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * PsiClass是否Controller实例
     *
     * @param psiClass psi级
     * @return boolean
     * @author mabin
     * @date 2024/04/22 15:33
     */
    public static boolean isController(PsiClass psiClass) {
        for (PsiAnnotation psiAnnotation : psiClass.getAnnotations()) {
            if (StringUtils.equalsAny(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.CONTROLLER_ANNOTATION.getName(), SpringAnnotationEnum.REST_CONTROLLER_ANNOTATION.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取注解属性值
     *
     * @param psiAnnotation psi注释
     * @param attrNames     属性名称: 获取第一个非空属性值返回
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:56
     */
    public static String getAnnotationAttributeValue(PsiAnnotation psiAnnotation, List<String> attrNames) {
        return getAnnotationAttributeValue(psiAnnotation, StringUtils.EMPTY, attrNames);
    }

    /**
     * 获取注解属性值
     *
     * @param psiAnnotation psi注释
     * @param defaultValue  默认值: 属性值均为空时返回
     * @param attrNames     属性名称: 获取第一个非空属性值返回
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:54
     */
    public static String getAnnotationAttributeValue(PsiAnnotation psiAnnotation, String defaultValue, List<String> attrNames) {
        String attributeValue = StringUtils.EMPTY;
        if (Objects.nonNull(psiAnnotation) && Objects.nonNull(attrNames) && !attrNames.isEmpty()) {
            for (String attrName : attrNames) {
                PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue(attrName);
                if (Objects.isNull(psiAnnotationAttributeValue)) {
                    continue;
                }
                attributeValue = washSpecialChar(psiAnnotationAttributeValue.getText());
                if (StringUtils.isNotBlank(attributeValue)) {
                    break;
                }
            }
        }
        return StringUtils.isBlank(attributeValue) ? defaultValue : attributeValue;
    }

    /**
     * 解析JavaDoc的desc说明
     *
     * @param elements 元素
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 10:43
     */
    public static String parseJavaDocDesc(List<PsiElement> elements) {
        return parseJavaDocDesc(elements, StringUtils.EMPTY);
    }

    /**
     * 解析JavaDoc的desc说明
     *
     * @param elements     元素
     * @param defaultValue 默认值
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/24 10:43
     */
    public static String parseJavaDocDesc(List<PsiElement> elements, String defaultValue) {
        if (Objects.nonNull(elements) && !elements.isEmpty()) {
            for (PsiElement element : elements) {
                if (!StringUtils.equalsIgnoreCase("PsiDocToken:DOC_COMMENT_DATA", element.toString())) {
                    continue;
                }
                String desc = element.getText().replaceAll("[/* \n]+", StringUtils.EMPTY);
                if (StringUtils.isNotBlank(desc)) {
                    return desc;
                }
            }
        }
        return defaultValue;
    }

    /**
     * 解析JavaDoc方法参数
     *
     * @param psiMethod psi法
     * @return {@link java.util.Map<java.lang.String,java.lang.String>}
     * @author mabin
     * @date 2024/04/24 11:01
     */
    public static Map<String, String> parseJavaDocMethodParams(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return MapUtil.newHashMap();
        }
        PsiDocComment psiMethodDocComment = psiMethod.getDocComment();
        if (Objects.isNull(psiMethodDocComment)) {
            return MapUtil.newHashMap();
        }
        Map<String, String> paramMap = MapUtil.newHashMap(true);
        for (PsiElement psiElement : psiMethodDocComment.getChildren()) {
            if (!StringUtils.equalsIgnoreCase("PsiDocTag:@param", psiElement.toString())) {
                continue;
            }
            String paramName = StringUtils.EMPTY;
            String paramData = StringUtils.EMPTY;
            for (PsiElement child : psiElement.getChildren()) {
                if (StringUtils.equalsIgnoreCase("PsiElement(DOC_PARAMETER_REF)", child.toString())) {
                    paramName = StringUtils.trim(child.getText());
                }
                if (StringUtils.equalsIgnoreCase("PsiDocToken:DOC_COMMENT_DATA", child.toString())) {
                    paramData = StringUtils.trim(child.getText());
                }
                if (StringUtils.isBlank(paramName)) {
                    continue;
                }
                paramMap.put(paramName, paramData);
            }
        }
        return paramMap;
    }


    /**
     * 获取Java文件
     *
     * @param project  项目
     * @param psiFiles psi文件
     * @return {@link java.util.List<com.intellij.psi.PsiJavaFile>}
     * @author mabin
     * @date 2024/05/11 14:12
     */
    public static List<PsiJavaFile> getPsiJavaFiles(Project project, VirtualFile[] psiFiles) {
        List<PsiJavaFile> files = Lists.newArrayListWithExpectedSize(psiFiles.length);
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile f : psiFiles) {
            if (f.isDirectory()) {
                VirtualFile[] children = f.getChildren();
                List<PsiJavaFile> theFiles = getPsiJavaFiles(project, children);
                files.addAll(theFiles);
                continue;
            }
            PsiFile file = psiManager.findFile(f);
            if (file instanceof PsiJavaFileImpl) {
                files.add((PsiJavaFileImpl) file);
            }
        }
        return files;
    }

    /**
     * 获取PsiClass
     *
     * @param psiJavaFiles psi java文件
     * @return {@link List< PsiClass>}
     * @author mabin
     * @date 2024/05/11 14:12
     */
    public static List<PsiClass> getPsiClassByFile(List<PsiJavaFile> psiJavaFiles) {
        List<PsiClass> psiClassList = Lists.newArrayListWithCapacity(psiJavaFiles.size());
        for (PsiJavaFile psiJavaFile : psiJavaFiles) {
            Arrays.stream(psiJavaFile.getClasses())
                    .filter(o -> !o.isInterface()
                            && o.getModifierList() != null
                            && o.getModifierList().hasModifierProperty(PsiModifier.PUBLIC))
                    .findFirst().ifPresent(psiClassList::add);
        }
        return psiClassList;
    }





    /**
     * 清洗特殊字符
     *
     * @param value 价值
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/22 15:48
     */
    private static String washSpecialChar(String value) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        value = StringUtils.equals(value, "\"\"") ? StringUtils.EMPTY : value;
        return StringUtils.contains(value, "\"") ? StringUtils.replace(value, "\"", StringUtils.EMPTY) : value;
    }

}
